package com.jeecms.bbs.action.front;

import static com.jeecms.bbs.Constants.TPLDIR_FORUM;
import static com.jeecms.bbs.Constants.TPLDIR_TOPIC;

import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.jeecms.bbs.cache.TopicCountEhCache;
import com.jeecms.bbs.entity.BbsCategory;
import com.jeecms.bbs.entity.BbsForum;
import com.jeecms.bbs.entity.BbsTopic;
import com.jeecms.bbs.entity.BbsUser;
import com.jeecms.bbs.entity.BbsUserGroup;
import com.jeecms.bbs.manager.BbsCategoryMng;
import com.jeecms.bbs.manager.BbsConfigMng;
import com.jeecms.bbs.manager.BbsForumMng;
import com.jeecms.bbs.manager.BbsPostMng;
import com.jeecms.bbs.manager.BbsTopicMng;
import com.jeecms.bbs.web.CmsUtils;
import com.jeecms.bbs.web.FrontUtils;
import com.jeecms.common.web.RequestUtils;
import com.jeecms.core.entity.CmsSite;

@Controller
public class BbsTopicAct {
	private static final Logger log = LoggerFactory
			.getLogger(BbsTopicAct.class);

	public static final String TPL_TOPICADD = "tpl.topicadd";
	public static final String TPL_TOPICEDIT = "tpl.topicedit";
	public static final String TPL_NO_LOGIN = "tpl.nologin";
	public static final String TPL_GUANSHUI = "tpl.guanshui";
	public static final String TPL_TOPIC_MOVE = "tpl.topicmove";
	public static final String TPL_TOPIC_SHIELD = "tpl.topicshield";
	public static final String TPL_TOPIC_LOCK = "tpl.topiclock";
	public static final String TPL_TOPIC_UPORDOWN = "tpl.topicupordown";
	public static final String TPL_TOPIC_PRIME = "tpl.topicprime";
	public static final String TPL_TOPIC_UPTOP = "tpl.topicuptop";
	public static final String TPL_TOPIC_HIGHLIGHT = "tpl.topichighlight";
	public static final String TPL_DAY_TOPIC = "tpl.daytopic";
	public static final String TPL_NO_VIEW = "tpl.noview";

	@RequestMapping("/topic/v_add{forumId}.jspx")
	public String add(@PathVariable Integer forumId, Integer fid,
			HttpServletRequest request, ModelMap model) {
		CmsSite site = CmsUtils.getSite(request);
		BbsUser user = CmsUtils.getUser(request);
		FrontUtils.frontData(request, model, site);
		if (user == null) {
			return FrontUtils.getTplPath(request, site.getSolutionPath(),
					TPLDIR_TOPIC, TPL_NO_LOGIN);
		}
		BbsForum forum = null;
		if (forumId != null) {
			model.put("forumId", forumId);
			forum = bbsForumMng.findById(forumId);
		} else {
			model.put("forumId", fid);
			forum = bbsForumMng.findById(fid);
		}
		String msg = checkTopic(forum, user, site);
		if (msg != null) {
			model.put("msg", msg);
			return FrontUtils.getTplPath(request, site.getSolutionPath(),
					TPLDIR_TOPIC, TPL_NO_VIEW);
		}
		return FrontUtils.getTplPath(request, site.getSolutionPath(),
				TPLDIR_TOPIC, TPL_TOPICADD);
	}

	@RequestMapping("/topic/o_save.jspx")
	public String save(Integer forumId, String title, String content,
			@RequestParam(value = "code", required = false) List<String> code,
			HttpServletRequest request, ModelMap model) {
		CmsSite site = CmsUtils.getSite(request);
		BbsUser user = CmsUtils.getUser(request);
		if (user == null) {
			return FrontUtils.getTplPath(request, site.getSolutionPath(),
					TPLDIR_TOPIC, TPL_NO_LOGIN);
		}
		BbsForum forum = bbsForumMng.findById(forumId);
		String msg = checkTopic(forum, user, site);
		if (msg != null) {
			model.put("msg", msg);
			return FrontUtils.getTplPath(request, site.getSolutionPath(),
					TPLDIR_TOPIC, TPL_NO_VIEW);
		}
		boolean flag = topicCountEhCache.getLastReply(user.getId(), 15);
		if (!flag) {
			return FrontUtils.getTplPath(request, site.getSolutionPath(),
					TPLDIR_TOPIC, TPL_GUANSHUI);
		}
		String ip = RequestUtils.getIpAddr(request);
		MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
		BbsTopic bean = manager.postTopic(user.getId(), site.getId(), forumId,
				title, content, ip, multipartRequest.getFiles("attachment"),
				code);
		log.info("save BbsTopic id={}", bean.getId());
		return "redirect:" + bean.getRedirectUrl();
	}

	@RequestMapping("/topic/v_moveInput.jspx")
	public String moveInput(Integer[] topicIds, Integer forumId,
			HttpServletRequest request, ModelMap model) {
		CmsSite site = CmsUtils.getSite(request);
		BbsUser user = CmsUtils.getUser(request);
		FrontUtils.frontData(request, model, site);
		if (user == null) {
			return FrontUtils.getTplPath(request, site.getSolutionPath(),
					TPLDIR_TOPIC, TPL_NO_LOGIN);
		}
		if (forumId != null) {
			BbsForum forum = bbsForumMng.findById(forumId);
			String msg = checkShield(forum, user, site);
			if (msg != null) {
				model.put("msg", msg);
				return FrontUtils.getTplPath(request, site.getSolutionPath(),
						TPLDIR_TOPIC, TPL_NO_VIEW);
			}
			model.put("forum", forum);
		}
		List<BbsCategory> categoryList = bbsCategoryMng.getList(site.getId());
		model.put("categoryList", categoryList);
		model.put("topicIds", topicIds);
		return FrontUtils.getTplPath(request, site.getSolutionPath(),
				TPLDIR_TOPIC, TPL_TOPIC_MOVE);
	}

	@RequestMapping("/topic/o_moveSubmit.jspx")
	public String moveSubmit(Integer[] topicIds, Integer moveTo,
			Integer forumId, String reason, HttpServletRequest request,
			ModelMap model) {
		CmsSite site = CmsUtils.getSite(request);
		BbsUser user = CmsUtils.getUser(request);
		if (user == null) {
			return FrontUtils.getTplPath(request, site.getSolutionPath(),
					TPLDIR_TOPIC, TPL_NO_LOGIN);
		}
		BbsForum forum = bbsForumMng.findById(forumId);
		String msg = checkShield(forum, user, site);
		if (msg != null) {
			model.put("msg", msg);
			return FrontUtils.getTplPath(request, site.getSolutionPath(),
					TPLDIR_TOPIC, TPL_NO_VIEW);
		}
		manager.move(topicIds, moveTo, reason, user);
		return "redirect:" + forum.getRedirectUrl();
	}

	@RequestMapping("/topic/v_shieldInput.jspx")
	public String shieldInput(Integer[] topicIds, Integer forumId,
			HttpServletRequest request, ModelMap model) {
		CmsSite site = CmsUtils.getSite(request);
		BbsUser user = CmsUtils.getUser(request);
		FrontUtils.frontData(request, model, site);
		if (user == null) {
			return FrontUtils.getTplPath(request, site.getSolutionPath(),
					TPLDIR_TOPIC, TPL_NO_LOGIN);
		}
		if (forumId != null) {
			BbsForum forum = bbsForumMng.findById(forumId);
			String msg = checkShield(forum, user, site);
			if (msg != null) {
				model.put("msg", msg);
				return FrontUtils.getTplPath(request, site.getSolutionPath(),
						TPLDIR_TOPIC, TPL_NO_VIEW);
			}
			model.put("forum", forum);
		}
		model.put("topicIds", topicIds);
		return FrontUtils.getTplPath(request, site.getSolutionPath(),
				TPLDIR_TOPIC, TPL_TOPIC_SHIELD);
	}

	@RequestMapping("/topic/o_shieldSubmit.jspx")
	public String shieldSubmit(Integer[] topicIds, Integer forumId,
			boolean shield, String reason, HttpServletRequest request,
			ModelMap model) {
		CmsSite site = CmsUtils.getSite(request);
		BbsUser user = CmsUtils.getUser(request);
		if (user == null) {
			return FrontUtils.getTplPath(request, site.getSolutionPath(),
					TPLDIR_TOPIC, TPL_NO_LOGIN);
		}
		BbsForum forum = bbsForumMng.findById(forumId);
		String msg = checkShield(forum, user, site);
		if (msg != null) {
			model.put("msg", msg);
			return FrontUtils.getTplPath(request, site.getSolutionPath(),
					TPLDIR_TOPIC, TPL_NO_VIEW);
		}
		manager.shieldOrOpen(topicIds, shield, reason, user);
		return "redirect:" + forum.getRedirectUrl();
	}

	@RequestMapping("/topic/v_lockInput.jspx")
	public String lockInput(Integer[] topicIds, Integer forumId,
			HttpServletRequest request, ModelMap model) {
		CmsSite site = CmsUtils.getSite(request);
		BbsUser user = CmsUtils.getUser(request);
		FrontUtils.frontData(request, model, site);
		if (user == null) {
			return FrontUtils.getTplPath(request, site.getSolutionPath(),
					TPLDIR_TOPIC, TPL_NO_LOGIN);
		}
		if (forumId != null) {
			BbsForum forum = bbsForumMng.findById(forumId);
			String msg = checkManager(forum, user, site);
			if (msg != null) {
				model.put("msg", msg);
				return FrontUtils.getTplPath(request, site.getSolutionPath(),
						TPLDIR_TOPIC, TPL_NO_VIEW);
			}
			model.put("forum", forum);
		}
		model.put("topicIds", topicIds);
		return FrontUtils.getTplPath(request, site.getSolutionPath(),
				TPLDIR_TOPIC, TPL_TOPIC_LOCK);
	}

	@RequestMapping("/topic/o_lockSumbit.jspx")
	public String lockSubmit(Integer[] topicIds, Integer forumId, boolean lock,
			String reason, HttpServletRequest request, ModelMap model) {
		CmsSite site = CmsUtils.getSite(request);
		BbsUser user = CmsUtils.getUser(request);
		if (user == null) {
			return FrontUtils.getTplPath(request, site.getSolutionPath(),
					TPLDIR_TOPIC, TPL_NO_LOGIN);
		}
		BbsForum forum = bbsForumMng.findById(forumId);
		String msg = checkManager(forum, user, site);
		if (msg != null) {
			model.put("msg", msg);
			return FrontUtils.getTplPath(request, site.getSolutionPath(),
					TPLDIR_TOPIC, TPL_NO_VIEW);
		}
		manager.lockOrOpen(topicIds, lock, reason, user);
		return "redirect:" + forum.getRedirectUrl();
	}

	@RequestMapping("/topic/v_upordownInput.jspx")
	public String upOrDownInput(Integer[] topicIds, Integer forumId,
			HttpServletRequest request, ModelMap model) {
		CmsSite site = CmsUtils.getSite(request);
		BbsUser user = CmsUtils.getUser(request);
		FrontUtils.frontData(request, model, site);
		if (user == null) {
			return FrontUtils.getTplPath(request, site.getSolutionPath(),
					TPLDIR_TOPIC, TPL_NO_LOGIN);
		}
		if (forumId != null) {
			BbsForum forum = bbsForumMng.findById(forumId);
			String msg = checkManager(forum, user, site);
			if (msg != null) {
				model.put("msg", msg);
				return FrontUtils.getTplPath(request, site.getSolutionPath(),
						TPLDIR_TOPIC, TPL_NO_VIEW);
			}
			model.put("forum", forum);
		}
		model.put("topicIds", topicIds);
		return FrontUtils.getTplPath(request, site.getSolutionPath(),
				TPLDIR_TOPIC, TPL_TOPIC_UPORDOWN);
	}

	@RequestMapping("/topic/o_upordownSubmit.jspx")
	public String upOrDownSubmit(Integer[] topicIds, Integer forumId,
			Date time, String reason, HttpServletRequest request, ModelMap model) {
		CmsSite site = CmsUtils.getSite(request);
		BbsUser user = CmsUtils.getUser(request);
		if (user == null) {
			return FrontUtils.getTplPath(request, site.getSolutionPath(),
					TPLDIR_TOPIC, TPL_NO_LOGIN);
		}
		BbsForum forum = bbsForumMng.findById(forumId);
		String msg = checkManager(forum, user, site);
		if (msg != null) {
			model.put("msg", msg);
			return FrontUtils.getTplPath(request, site.getSolutionPath(),
					TPLDIR_TOPIC, TPL_NO_VIEW);
		}
		manager.upOrDown(topicIds, time, reason, user);
		return "redirect:" + forum.getRedirectUrl();
	}

	@RequestMapping("/topic/v_primeInput.jspx")
	public String primeInput(Integer[] topicIds, Integer forumId,
			HttpServletRequest request, ModelMap model) {
		CmsSite site = CmsUtils.getSite(request);
		BbsUser user = CmsUtils.getUser(request);
		FrontUtils.frontData(request, model, site);
		if (user == null) {
			return FrontUtils.getTplPath(request, site.getSolutionPath(),
					TPLDIR_TOPIC, TPL_NO_LOGIN);
		}
		if (forumId != null) {
			BbsForum forum = bbsForumMng.findById(forumId);
			String msg = checkManager(forum, user, site);
			if (msg != null) {
				model.put("msg", msg);
				return FrontUtils.getTplPath(request, site.getSolutionPath(),
						TPLDIR_TOPIC, TPL_NO_VIEW);
			}
			model.put("forum", forum);
		}
		model.put("topicIds", topicIds);
		return FrontUtils.getTplPath(request, site.getSolutionPath(),
				TPLDIR_TOPIC, TPL_TOPIC_PRIME);
	}

	@RequestMapping("/topic/o_primeSubmit.jspx")
	public String primeSubmit(Integer[] topicIds, Integer forumId,
			short primeLevel, String reason, HttpServletRequest request,
			ModelMap model) {
		CmsSite site = CmsUtils.getSite(request);
		BbsUser user = CmsUtils.getUser(request);
		if (user == null) {
			return FrontUtils.getTplPath(request, site.getSolutionPath(),
					TPLDIR_TOPIC, TPL_NO_LOGIN);
		}
		BbsForum forum = bbsForumMng.findById(forumId);
		String msg = checkManager(forum, user, site);
		if (msg != null) {
			model.put("msg", msg);
			return FrontUtils.getTplPath(request, site.getSolutionPath(),
					TPLDIR_TOPIC, TPL_NO_VIEW);
		}
		manager.prime(topicIds, primeLevel, reason, user);
		return "redirect:" + forum.getRedirectUrl();
	}

	@RequestMapping("/topic/v_upTopInput.jspx")
	public String upTopInput(Integer[] topicIds, Integer forumId,
			HttpServletRequest request, ModelMap model) {
		CmsSite site = CmsUtils.getSite(request);
		BbsUser user = CmsUtils.getUser(request);
		FrontUtils.frontData(request, model, site);
		if (user == null) {
			return FrontUtils.getTplPath(request, site.getSolutionPath(),
					TPLDIR_TOPIC, TPL_NO_LOGIN);
		}
		if (forumId != null) {
			BbsForum forum = bbsForumMng.findById(forumId);
			String msg = checkUpTop(forum, user, site, BbsTopic.NORMAL);
			if (msg != null) {
				model.put("msg", msg);
				return FrontUtils.getTplPath(request, site.getSolutionPath(),
						TPLDIR_TOPIC, TPL_NO_VIEW);
			}
			model.put("forum", forum);
		}
		model.put("topicIds", topicIds);
		return FrontUtils.getTplPath(request, site.getSolutionPath(),
				TPLDIR_TOPIC, TPL_TOPIC_UPTOP);
	}

	@RequestMapping("/topic/o_upTopSubmit.jspx")
	public String upTopSubmit(Integer[] topicIds, Integer forumId,
			short topLevel, String reason, HttpServletRequest request,
			ModelMap model) {
		CmsSite site = CmsUtils.getSite(request);
		BbsUser user = CmsUtils.getUser(request);
		if (user == null) {
			return FrontUtils.getTplPath(request, site.getSolutionPath(),
					TPLDIR_TOPIC, TPL_NO_LOGIN);
		}
		BbsForum forum = bbsForumMng.findById(forumId);
		String msg = checkUpTop(forum, user, site, BbsTopic.NORMAL);
		if (msg != null) {
			model.put("msg", msg);
			return FrontUtils.getTplPath(request, site.getSolutionPath(),
					TPLDIR_TOPIC, TPL_NO_VIEW);
		}
		manager.upTop(topicIds, topLevel, reason, user);
		return "redirect:" + forum.getRedirectUrl();
	}

	@RequestMapping("/topic/v_highlightInput.jspx")
	public String highlightInput(Integer[] topicIds, Integer forumId,
			HttpServletRequest request, ModelMap model) {
		CmsSite site = CmsUtils.getSite(request);
		BbsUser user = CmsUtils.getUser(request);
		FrontUtils.frontData(request, model, site);
		if (user == null) {
			return FrontUtils.getTplPath(request, site.getSolutionPath(),
					TPLDIR_TOPIC, TPL_NO_LOGIN);
		}
		if (forumId != null) {
			BbsForum forum = bbsForumMng.findById(forumId);
			String msg = checkManager(forum, user, site);
			if (msg != null) {
				model.put("msg", msg);
				return FrontUtils.getTplPath(request, site.getSolutionPath(),
						TPLDIR_TOPIC, TPL_NO_VIEW);
			}
			model.put("forum", forum);
		}
		model.put("topicIds", topicIds);
		return FrontUtils.getTplPath(request, site.getSolutionPath(),
				TPLDIR_TOPIC, TPL_TOPIC_HIGHLIGHT);
	}

	@RequestMapping("/topic/o_highlightSubmit.jspx")
	public String highlightSubmit(Integer[] topicIds, Integer forumId,
			String color, boolean bold, boolean italic, Date time,
			String reason, HttpServletRequest request, ModelMap model) {
		CmsSite site = CmsUtils.getSite(request);
		BbsUser user = CmsUtils.getUser(request);
		if (user == null) {
			return FrontUtils.getTplPath(request, site.getSolutionPath(),
					TPLDIR_TOPIC, TPL_NO_LOGIN);
		}
		BbsForum forum = bbsForumMng.findById(forumId);
		String msg = checkManager(forum, user, site);
		if (msg != null) {
			model.put("msg", msg);
			return FrontUtils.getTplPath(request, site.getSolutionPath(),
					TPLDIR_TOPIC, TPL_NO_VIEW);
		}
		manager.highlight(topicIds, color, bold, italic, time, reason, user);
		return "redirect:" + forum.getRedirectUrl();
	}

	@RequestMapping("/topic/v_searchDayTopic.jspx")
	public String searchByDay(Integer forumId, Integer day,
			HttpServletRequest request, ModelMap model) {
		CmsSite site = CmsUtils.getSite(request);
		BbsUser user = CmsUtils.getUser(request);
		FrontUtils.frontData(request, model, site);
		BbsForum forum = bbsForumMng.findById(forumId);
		boolean check = checkView(forum, user, site);
		if (!check) {
			model.put("msg", "你所在会员组没有访问该地址的权限");
			return FrontUtils.getTplPath(request, site.getSolutionPath(),
					TPLDIR_FORUM, TPL_NO_VIEW);
		}
		model.put("manager", checkManager(forum, user, site) == null ? true
				: false);
		model.put("uptop", checkUpTop(forum, user, site));
		model.put("sheild", checkShield(forum, user, site) == null ? true
				: false);
		model.put("moderators", checkModerators(forum, user, site));
		model.put("forum", forum);
		model.put("day", day);
		FrontUtils.frontPageData(request, model);
		return FrontUtils.getTplPath(request, site.getSolutionPath(),
				TPLDIR_TOPIC, TPL_DAY_TOPIC);
	}

	@RequestMapping("/topic/o_delete.jspx")
	public String delete(Integer[] topicIds, Integer pageNo,
			HttpServletRequest request, ModelMap model) {
		BbsTopic[] beans = manager.deleteByIds(topicIds);
		BbsForum forum = beans[0].getForum();
		for (BbsTopic bean : beans) {
			log.info("delete BbsTopic id={}", bean.getId());
		}
		return "redirect:" + forum.getRedirectUrl();
	}

	private String checkTopic(BbsForum forum, BbsUser user, CmsSite site) {
		if (forum.getGroupTopics() == null) {
			return "您所在会员组没有访问该地址的权限";
		} else {
			BbsUserGroup group = null;
			if (user == null) {
				group = bbsConfigMng.findById(site.getId()).getDefaultGroup();
			} else {
				if (user.getProhibit()) {
					return "您已经被禁言，无权访问该页面";
				}
				group = user.getGroup();
			}
			if (group == null) {
				return "您所在会员组没有访问该地址的权限";
			}
			if (!group.allowTopic()) {
				return "您所在会员组没有访问该地址的权限";
			}
			if (forum.getGroupTopics().indexOf("," + group.getId() + ",") == -1) {
				return "您所在会员组没有访问该地址的权限";
			}
			if (!group.checkPostToday(user.getPostToday())) {
				return "您今日发帖量已经达到最大";
			}
		}
		return null;
	}

	private String checkManager(BbsForum forum, BbsUser user, CmsSite site) {
		if (forum.getGroupTopics() == null) {
			return "您所在会员组没有访问该地址的权限";
		} else {
			BbsUserGroup group = null;
			if (user == null) {
				group = bbsConfigMng.findById(site.getId()).getDefaultGroup();
			} else {
				group = user.getGroup();
			}
			if (group == null) {
				return "您所在会员组没有访问该地址的权限";
			}
			if (!group.allowTopic()) {
				return "您所在会员组没有访问该地址的权限";
			}
			if (forum.getGroupTopics().indexOf("," + group.getId() + ",") == -1) {
				return "您所在会员组没有访问该地址的权限";
			}
			if (!group.hasRight(forum, user)) {
				return "您所在会员组没有访问该地址的权限";
			}
			if (!group.topicManage()) {
				return "您所在会员组没有访问该地址的权限";
			}
		}
		return null;
	}

	private String checkUpTop(BbsForum forum, BbsUser user, CmsSite site,
			short topLevel) {
		if (forum.getGroupTopics() == null) {
			return "您所在会员组没有访问该地址的权限";
		} else {
			BbsUserGroup group = null;
			if (user == null) {
				group = bbsConfigMng.findById(site.getId()).getDefaultGroup();
			} else {
				group = user.getGroup();
			}
			if (group == null) {
				return "您所在会员组没有访问该地址的权限";
			}
			if (!group.allowTopic()) {
				return "您所在会员组没有访问该地址的权限";
			}
			if (forum.getGroupTopics().indexOf("," + group.getId() + ",") == -1) {
				return "您所在会员组没有访问该地址的权限";
			}
			if (!group.hasRight(forum, user)) {
				return "您所在会员组没有访问该地址的权限";
			}
			if (group.topicTop() == 0) {
				return "您所在会员组没有访问该地址的权限";
			}
			if (topLevel > 0) {
				if (group.topicTop() < topLevel) {
					return "您所在会员组没有访问该地址的权限";
				}
			}
		}
		return null;
	}

	private String checkShield(BbsForum forum, BbsUser user, CmsSite site) {
		if (forum.getGroupTopics() == null) {
			return "您所在会员组没有访问该地址的权限";
		} else {
			BbsUserGroup group = null;
			if (user == null) {
				group = bbsConfigMng.findById(site.getId()).getDefaultGroup();
			} else {
				group = user.getGroup();
			}
			if (group == null) {
				return "您所在会员组没有访问该地址的权限";
			}
			if (!group.allowTopic()) {
				return "您所在会员组没有访问该地址的权限";
			}
			if (forum.getGroupTopics().indexOf("," + group.getId() + ",") == -1) {
				return "您所在会员组没有访问该地址的权限";
			}
			if (!group.hasRight(forum, user)) {
				return "您所在会员组没有访问该地址的权限";
			}
			if (!group.topicShield()) {
				return "您所在会员组没有访问该地址的权限";
			}
		}
		return null;
	}

	private boolean checkView(BbsForum forum, BbsUser user, CmsSite site) {
		if (forum.getGroupViews() == null) {
			return false;
		} else {
			BbsUserGroup group = null;
			if (user == null) {
				group = bbsConfigMng.findById(site.getId()).getDefaultGroup();
			} else {
				group = user.getGroup();
			}
			if (group == null) {
				return false;
			}
			if (forum.getGroupViews().indexOf("," + group.getId() + ",") == -1) {
				return false;
			}
		}
		return true;
	}

	private boolean checkUpTop(BbsForum forum, BbsUser user, CmsSite site) {
		if (forum.getGroupViews() == null) {
			return false;
		} else {
			BbsUserGroup group = null;
			if (user == null) {
				group = bbsConfigMng.findById(site.getId()).getDefaultGroup();
			} else {
				group = user.getGroup();
			}
			if (group == null) {
				return false;
			}
			if (!group.hasRight(forum, user)) {
				return false;
			}
			if (group.topicTop() == 0) {
				return false;
			}
		}
		return true;
	}

	private boolean checkModerators(BbsForum forum, BbsUser user, CmsSite site) {
		if (forum.getGroupViews() == null) {
			return false;
		} else {
			BbsUserGroup group = null;
			if (user == null) {
				group = bbsConfigMng.findById(site.getId()).getDefaultGroup();
			} else {
				group = user.getGroup();
			}
			if (group == null) {
				return false;
			}
			if (!group.hasRight(forum, user)) {
				return false;
			}
		}
		return true;
	}

	@Autowired
	private BbsTopicMng manager;
	@Autowired
	private BbsPostMng bbsPostMng;
	@Autowired
	private BbsForumMng bbsForumMng;
	@Autowired
	private BbsCategoryMng bbsCategoryMng;
	@Autowired
	private BbsConfigMng bbsConfigMng;
	@Autowired
	private TopicCountEhCache topicCountEhCache;
}
