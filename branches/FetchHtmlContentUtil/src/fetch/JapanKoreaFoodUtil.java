package fetch;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * 日韩料理
 * 
 */
public class JapanKoreaFoodUtil {

	public static String getFoodListUrl = "http://caipu.supfree.net/door.asp";// 国家-食物列表
	public static String getDetailUrl = "http://caipu.supfree.net/windows.asp";// 详细页
	public static String Referer = "http://fengsu.supfree.net/";
	public static String Host = "fengsu.supfree.net";
	public static String UTF8 = "UTF-8";
	public static String GB2312 = "GB2312";
	public static String GBK = "GBK";
	public static Map<String, List<KeyValue>> Country_food_Map = new HashMap<String, List<KeyValue>>();

	static {
		/**
		 * 下面抓取到的内容，有些识别不了，显示成 �
		 */
		List<KeyValue> list = new ArrayList<KeyValue>();
		list.add(new KeyValue("8589","生鱼四喜饭"));
		list.add(new KeyValue("8590","五色寿司饭"));
		list.add(new KeyValue("8591","腐皮牛肉寿司"));
		list.add(new KeyValue("8592","日式三文鱼生鱼片"));
		list.add(new KeyValue("8593","茶碗蒸"));
		list.add(new KeyValue("8594","酸辣蒸日式豆腐"));
		list.add(new KeyValue("8595","扬出豆腐"));
		list.add(new KeyValue("8596","味噌汤"));
		list.add(new KeyValue("8597","日式凉面"));
		list.add(new KeyValue("8598","日式炸猪排"));
		list.add(new KeyValue("8599","东京蛋卷寿司"));
		list.add(new KeyValue("8600","火腿蒸土豆"));
		list.add(new KeyValue("8601","日式叉烧肉"));
		list.add(new KeyValue("8602","五彩炒饭"));
		list.add(new KeyValue("8603","蔬果寿司"));
		list.add(new KeyValue("8604","日式红烧鱼头"));
		list.add(new KeyValue("8605","辣汁三文鱼"));
		list.add(new KeyValue("8606","三文鱼茶泡饭"));
		list.add(new KeyValue("8607","日式乌冬面汤"));
		list.add(new KeyValue("8608","高丽菜卷"));
		list.add(new KeyValue("8609","冷豆腐"));
		list.add(new KeyValue("8610","日式冷素面"));
		list.add(new KeyValue("8611","日式蛋卷"));
		list.add(new KeyValue("8612","日式锅烧面"));
		list.add(new KeyValue("8613","菜花咖喱虾"));
		list.add(new KeyValue("8614","洋葱金枪鱼炒蛋"));
		list.add(new KeyValue("8615","素便当"));
		list.add(new KeyValue("8616","洋兰三文鱼"));
		list.add(new KeyValue("8617","鸡肉治部煮"));
		list.add(new KeyValue("8618","生鱼片拉面"));
		list.add(new KeyValue("8619","日式海鲜炒面"));
		list.add(new KeyValue("8620","鲳鱼扬"));
		list.add(new KeyValue("8621","土豆奶汁烤菜"));
		list.add(new KeyValue("8622","金黄沙棘日本豆腐"));
		list.add(new KeyValue("8623","筑前煮"));
		list.add(new KeyValue("8624","翡翠鱿鱼卷"));
		list.add(new KeyValue("8625","天妇罗大虾面"));
		list.add(new KeyValue("8626","豆沙白玉团子"));
		list.add(new KeyValue("8627","绿意通心粉"));
		list.add(new KeyValue("8628","鱿鱼拌菜"));
		list.add(new KeyValue("8629","鲜杂果蒜茸包烟鳗鱼"));
		list.add(new KeyValue("8630","荷兰豆烧麦"));
		list.add(new KeyValue("8631","日式炸酿鲜冬菇"));
		list.add(new KeyValue("8632","日式漆匠萝卜"));
		list.add(new KeyValue("8633","日式蟹柳扒鲜草菇"));
		list.add(new KeyValue("8634","牛肉柳川风"));
		list.add(new KeyValue("8635","葱香豆皮"));
		list.add(new KeyValue("8636","圆葱饭团"));
		list.add(new KeyValue("8637","橙汁煎地瓜"));
		list.add(new KeyValue("8638","牛蒡片泡菜"));
		list.add(new KeyValue("8639","海鳗鸡骨汤"));
		list.add(new KeyValue("8640","面豉烧龙虾及生豪伴海胆"));
		list.add(new KeyValue("8641","漆匠萝卜"));
		list.add(new KeyValue("8642","沙律海鲜卷"));
		list.add(new KeyValue("8643","茶壶蒸海鲜"));
		list.add(new KeyValue("8644","日式肉松饭团"));
		list.add(new KeyValue("8645","葱油鸡便当"));
		list.add(new KeyValue("8646","素�菜心"));
		list.add(new KeyValue("8647","槟榔排骨锅"));
		list.add(new KeyValue("8648","鲑鱼生鱼片沙拉"));
		list.add(new KeyValue("8649","��牛腩便�"));
		list.add(new KeyValue("8650","青红椒炒烤麸"));
		list.add(new KeyValue("8651","京酱牛肉丝便当"));
		list.add(new KeyValue("8652","京�牛肉�便�"));
		list.add(new KeyValue("8653","木瓜炖生鱼"));
		list.add(new KeyValue("8654","生煎皈鱼"));
		list.add(new KeyValue("8655","菠萝蛋卷"));
		list.add(new KeyValue("8656","香荽鱼松酿银萝"));
		list.add(new KeyValue("8657","什锦海鲜锅"));
		list.add(new KeyValue("8658","日式炸豆腐"));
		list.add(new KeyValue("8659","三文鱼寿司卷"));
		list.add(new KeyValue("8660","海鲜小火锅"));
		list.add(new KeyValue("8661","蔬菜盒"));
		list.add(new KeyValue("8662","沙棘日本豆腐"));
		list.add(new KeyValue("8663","肉松紫菜卷"));
		list.add(new KeyValue("8664","香辣豆瓣鱼"));
		list.add(new KeyValue("8665","神户牛柳粒"));
		list.add(new KeyValue("8666","吉列生蚝"));
		list.add(new KeyValue("8667","日本寿司(推荐)"));
		list.add(new KeyValue("8668","紫菜卷寿司"));
		list.add(new KeyValue("8669","日式炒面"));
		list.add(new KeyValue("8670","日式蒸鱼"));
		list.add(new KeyValue("8671","豆腐皮寿司"));
		list.add(new KeyValue("8672","紫菜包饭"));
		list.add(new KeyValue("8673","百合玉子虾球"));
		list.add(new KeyValue("8674","烧日式肉松饭团"));
		list.add(new KeyValue("8675","红烧日本豆腐"));
		list.add(new KeyValue("8676","日式蔬菜咖喱"));
		list.add(new KeyValue("8677","咕�肉便当"));
		list.add(new KeyValue("8678","甜汁三文鱼"));
		list.add(new KeyValue("8679","三宝饭便当"));
		list.add(new KeyValue("8680","蟹柳蛋羹"));
		list.add(new KeyValue("8681","虾仁凉拌粉丝"));
		list.add(new KeyValue("8682","白辣鸡排酱"));
		list.add(new KeyValue("8683","秀珍菇味噌汤"));
		list.add(new KeyValue("8684","手握寿司"));
		list.add(new KeyValue("8685","水滴寿司"));
		list.add(new KeyValue("8686","什锦寿司卷"));
		list.add(new KeyValue("8687","出汁豆腐"));
		list.add(new KeyValue("8688","土豆可乐饼"));
		Country_food_Map.put("23",list);
		list = new ArrayList<KeyValue>();
		list.add(new KeyValue("8689","香菇青椒串烤"));
		list.add(new KeyValue("8690","嫩南瓜煎饼"));
		list.add(new KeyValue("8691","韩国炒粉条"));
		list.add(new KeyValue("8692","番茄小白菜"));
		list.add(new KeyValue("8693","牛骨汤面"));
		list.add(new KeyValue("8694","蕃茄咖喱烩蔬菜"));
		list.add(new KeyValue("8695","红枣蜜饯"));
		list.add(new KeyValue("8696","韩式炒饭"));
		list.add(new KeyValue("8697","总�三明治(2)"));
		list.add(new KeyValue("8698","蒸人参鸡"));
		list.add(new KeyValue("8699","白菜泡菜"));
		list.add(new KeyValue("8700","麻辣肚片"));
		list.add(new KeyValue("8701","鱿鱼卷"));
		list.add(new KeyValue("8702","捞面条"));
		list.add(new KeyValue("8703","圆糕"));
		list.add(new KeyValue("8704","酱汤泡饭"));
		list.add(new KeyValue("8705","通英拌饭"));
		list.add(new KeyValue("8706","韩国牛肉饼"));
		list.add(new KeyValue("8707","如意蛋卷"));
		list.add(new KeyValue("8708","炖年糕"));
		list.add(new KeyValue("8709","鸡肉炖土豆"));
		list.add(new KeyValue("8710","杂菜"));
		list.add(new KeyValue("8711","韩式牛尾汤"));
		list.add(new KeyValue("8712","酱猪排"));
		list.add(new KeyValue("8713","韩国烤肉火锅"));
		list.add(new KeyValue("8714","韩国烤牛肉"));
		list.add(new KeyValue("8715","腌白菜"));
		list.add(new KeyValue("8716","韩式红烧牛小排"));
		list.add(new KeyValue("8717","小白萝卜泡菜"));
		list.add(new KeyValue("8718","开城炖萝卜"));
		list.add(new KeyValue("8719","黄瓜炒猪肉片"));
		list.add(new KeyValue("8720","茄子泡菜"));
		list.add(new KeyValue("8721","咸菜"));
		list.add(new KeyValue("8722","炸琵琶虾"));
		list.add(new KeyValue("8723","粘糕汤"));
		list.add(new KeyValue("8724","牡蛎饭"));
		list.add(new KeyValue("8725","黄豆芽杂菜"));
		list.add(new KeyValue("8726","烤牛肉"));
		list.add(new KeyValue("8727","包泡菜"));
		list.add(new KeyValue("8728","冻土豆松糕"));
		list.add(new KeyValue("8729","韩味泡菜锅"));
		list.add(new KeyValue("8730","生拌百叶"));
		list.add(new KeyValue("8731","韩式芝麻冷汤"));
		list.add(new KeyValue("8732","土豆汤元"));
		list.add(new KeyValue("8733","尖椒炖鱼"));
		list.add(new KeyValue("8734","小黄瓜泡菜"));
		list.add(new KeyValue("8735","南瓜煳煳"));
		list.add(new KeyValue("8736","清曲酱汤"));
		list.add(new KeyValue("8737","芹菜应时泡菜"));
		list.add(new KeyValue("8738","烤牡蛎串"));
		list.add(new KeyValue("8739","东来葱煎饼"));
		list.add(new KeyValue("8740","田螺汤"));
		list.add(new KeyValue("8741","韩国烤鸡肉"));
		list.add(new KeyValue("8742","酱鸡"));
		list.add(new KeyValue("8743","韩国参鸡汤"));
		list.add(new KeyValue("8744","牛杂碎汤"));
		list.add(new KeyValue("8745","假祭食"));
		list.add(new KeyValue("8746","生鱿鱼片"));
		list.add(new KeyValue("8747","清炖狗肉"));
		list.add(new KeyValue("8748","红蛤蜊"));
		list.add(new KeyValue("8749","芥茉菜"));
		list.add(new KeyValue("8750","水梨辣泡菜"));
		list.add(new KeyValue("8751","冻土豆黄豆面条"));
		list.add(new KeyValue("8752","一品鲜贝"));
		list.add(new KeyValue("8753","韩国泡菜烧臭豆腐"));
		list.add(new KeyValue("8754","韩国烤沙参"));
		list.add(new KeyValue("8755","溜鲈鱼"));
		list.add(new KeyValue("8756","高粱煎豆包儿"));
		list.add(new KeyValue("8757","煮兔腿"));
		list.add(new KeyValue("8758","紫菜蛋卷"));
		list.add(new KeyValue("8759","茄子田乐烧"));
		list.add(new KeyValue("8760","萝卜片鱼汤"));
		list.add(new KeyValue("8761","清蒸竹笋"));
		list.add(new KeyValue("8762","炸指盖"));
		list.add(new KeyValue("8763","干烧芋头条"));
		list.add(new KeyValue("8764","荡平菜"));
		list.add(new KeyValue("8765","南瓜泡菜"));
		list.add(new KeyValue("8766","牛蒡泡菜"));
		list.add(new KeyValue("8767","药参鸡汤"));
		list.add(new KeyValue("8768","西瓜皮泡菜"));
		list.add(new KeyValue("8769","罐子狗肉"));
		list.add(new KeyValue("8770","煎烧鸡肉"));
		list.add(new KeyValue("8771","淡小萝卜水泡菜"));
		list.add(new KeyValue("8772","高丽菜结辣泡菜"));
		list.add(new KeyValue("8773","河蟹烧年糕"));
		list.add(new KeyValue("8774","大黄瓜泡菜"));
		list.add(new KeyValue("8775","辣葱泡菜"));
		list.add(new KeyValue("8776","米浆白菜泡菜"));
		list.add(new KeyValue("8777","杨梅虾球"));
		list.add(new KeyValue("8778","拌乔麦冷粉"));
		list.add(new KeyValue("8779","牛头肉片"));
		list.add(new KeyValue("8780","白果烧牛肉"));
		list.add(new KeyValue("8781","柿饼汁"));
		list.add(new KeyValue("8782","紫生菜泡菜"));
		list.add(new KeyValue("8783","鳞片辣萝卜泡菜"));
		list.add(new KeyValue("8784","辣韭菜结泡菜"));
		list.add(new KeyValue("8785","牛肉泡白菜"));
		list.add(new KeyValue("8786","笋丝泡菜"));
		list.add(new KeyValue("8787","红椒�时蔬"));
		list.add(new KeyValue("8788","醋味黄瓜泡菜"));
		list.add(new KeyValue("8789","双丝泡菜"));
		list.add(new KeyValue("8790","素泡白菜"));
		list.add(new KeyValue("8791","鸡丝炒海螺"));
		list.add(new KeyValue("8792","苹果柠檬泡菜"));
		list.add(new KeyValue("8793","章鱼粥"));
		list.add(new KeyValue("8794","黄瓜鱼鳞泡菜"));
		list.add(new KeyValue("8795","豆叶泡菜"));
		list.add(new KeyValue("8796","蜜汁�龙雪"));
		list.add(new KeyValue("8797","芝麻叶泡菜"));
		list.add(new KeyValue("8798","荏子鸡汤"));
		list.add(new KeyValue("8799","素泡什锦"));
		list.add(new KeyValue("8800","水参蜜饯"));
		list.add(new KeyValue("8801","淡小萝�水泡菜"));
		list.add(new KeyValue("8802","炖真鲷"));
		list.add(new KeyValue("8803","碳烤香菇"));
		list.add(new KeyValue("8804","辣脆素炒"));
		list.add(new KeyValue("8805","大白菜芝麻卷泡菜"));
		list.add(new KeyValue("8806","烤鸡配圆辣椒橄榄"));
		list.add(new KeyValue("8807","泡鱼辣椒"));
		list.add(new KeyValue("8808","鸡丝鸟窝"));
		list.add(new KeyValue("8809","苦瓜醋片泡菜"));
		list.add(new KeyValue("8810","安东蜜糯汤"));
		list.add(new KeyValue("8811","炖美沙参"));
		list.add(new KeyValue("8812","糖醋高丽菜"));
		list.add(new KeyValue("8813","凉拌高丽菜"));
		list.add(new KeyValue("8814","黑蛤蜊汤"));
		list.add(new KeyValue("8815","生菘木芽儿"));
		list.add(new KeyValue("8816","番茄焖明虾"));
		list.add(new KeyValue("8817","人�水泡菜"));
		list.add(new KeyValue("8818","柴鱼白菜卷泡菜"));
		list.add(new KeyValue("8819","走油鹑脯"));
		list.add(new KeyValue("8820","烤全笋"));
		list.add(new KeyValue("8821","大芥菜泡菜"));
		list.add(new KeyValue("8822","素鳗烧"));
		list.add(new KeyValue("8823","墨鱼仔包饭"));
		list.add(new KeyValue("8824","酸菜年糕卷"));
		list.add(new KeyValue("8825","臭豆腐专用泡菜"));
		list.add(new KeyValue("8826","糖醋红萝�"));
		list.add(new KeyValue("8827","白萝�青头泡菜"));
		list.add(new KeyValue("8828","凉拌桔梗"));
		list.add(new KeyValue("8829","泡菜炒素香肠"));
		list.add(new KeyValue("8830","酒酿白萝�泡菜"));
		list.add(new KeyValue("8831","高丽鱼条"));
		list.add(new KeyValue("8832","酱味烤海鱼"));
		list.add(new KeyValue("8833","糖醋高丽菜丝"));
		list.add(new KeyValue("8834","柳橙萝�泡菜"));
		list.add(new KeyValue("8835","黄萝�泡菜"));
		list.add(new KeyValue("8836","红白菱角烧"));
		list.add(new KeyValue("8837","玉笋煲"));
		list.add(new KeyValue("8838","福菜炒水蕨"));
		list.add(new KeyValue("8839","��冬瓜球"));
		list.add(new KeyValue("8840","三色椒百香果泡菜"));
		list.add(new KeyValue("8841","油菜花菇蕈泡菜"));
		list.add(new KeyValue("8842","杏鲍菇串烧"));
		list.add(new KeyValue("8843","�红萝�"));
		list.add(new KeyValue("8844","榴�富贵虾"));
		list.add(new KeyValue("8845","萝玛精选"));
		list.add(new KeyValue("8846","韩式炒年糕"));
		list.add(new KeyValue("8847","南瓜干年糕"));
		list.add(new KeyValue("8848","三色卷"));
		list.add(new KeyValue("8849","马铃薯手卷"));
		list.add(new KeyValue("8850","艾蒿年糕"));
		list.add(new KeyValue("8851","辣拌芥梗"));
		list.add(new KeyValue("8852","萝卜丝泡菜"));
		list.add(new KeyValue("8853","韩国绿豆饼"));
		list.add(new KeyValue("8854","海带芽冷汤"));
		list.add(new KeyValue("8855","松子茶"));
		list.add(new KeyValue("8856","拌冬粉"));
		list.add(new KeyValue("8857","卤牛蒡"));
		list.add(new KeyValue("8858","泡菜炒饭"));
		list.add(new KeyValue("8859","韩国煎豆腐"));
		list.add(new KeyValue("8860","山菜拌饭"));
		list.add(new KeyValue("8861","酱小鱿鱼"));
		list.add(new KeyValue("8862","海�泡菜"));
		list.add(new KeyValue("8863","带鱼南瓜汤"));
		list.add(new KeyValue("8864","辣�甜不辣"));
		list.add(new KeyValue("8865","韩国火锅沾料"));
		list.add(new KeyValue("8866","春川拌面"));
		list.add(new KeyValue("8867","南瓜膳"));
		list.add(new KeyValue("8868","龙凤汤"));
		list.add(new KeyValue("8869","辣海带芽泡菜"));
		list.add(new KeyValue("8870","兆朗年糕汤"));
		list.add(new KeyValue("8871","韭菜泡菜"));
		list.add(new KeyValue("8872","韩式樱桃鸡"));
		list.add(new KeyValue("8873","炸蒸狗肉"));
		list.add(new KeyValue("8874","艾蒿汤"));
		list.add(new KeyValue("8875","香烤甜不辣"));
		list.add(new KeyValue("8876","苦野菜泡菜"));
		list.add(new KeyValue("8877","水参"));
		list.add(new KeyValue("8878","泡菜饼"));
		list.add(new KeyValue("8879","朝鲜族打糕"));
		list.add(new KeyValue("8880","蜜汁照烧大塘虱鱼"));
		list.add(new KeyValue("8881","韩式泡菜拌饭"));
		list.add(new KeyValue("8882","韩式炒肉"));
		list.add(new KeyValue("8883","绿豆饼"));
		list.add(new KeyValue("8884","晋州拌饭"));
		list.add(new KeyValue("8885","拌乔麦凉粉"));
		list.add(new KeyValue("8886","红辣椒泡菜"));
		list.add(new KeyValue("8887","九折阪"));
		list.add(new KeyValue("8888","牡蛎蘸辣椒酱"));
		list.add(new KeyValue("8889","蕨菜汤"));
		list.add(new KeyValue("8890","烤辣酱猪肉"));
		list.add(new KeyValue("8891","韩国人�鸡"));
		list.add(new KeyValue("8892","泡菜汤"));
		list.add(new KeyValue("8893","炖牛尾汤"));
		list.add(new KeyValue("8894","蒜味辣泡菜"));
		list.add(new KeyValue("8895","青衣素心"));
		list.add(new KeyValue("8896","清蒸小鲍鱼"));
		list.add(new KeyValue("8897","方块辣萝卜泡菜"));
		list.add(new KeyValue("8898","韩国煮大虾"));
		list.add(new KeyValue("8899","黄豆芽汤饭"));
		list.add(new KeyValue("8900","紫菜卷"));
		list.add(new KeyValue("8901","辣白菜韭菜泡菜"));
		list.add(new KeyValue("8902","烤牛排"));
		list.add(new KeyValue("8903","韩式牛仔骨"));
		list.add(new KeyValue("8904","韩式泡菜豆腐汤"));
		list.add(new KeyValue("8905","小�瓜泡菜"));
		list.add(new KeyValue("8906","三种泡菜"));
		list.add(new KeyValue("8907","炖排骨"));
		list.add(new KeyValue("8908","千层白菜泡菜"));
		list.add(new KeyValue("8909","萝卜泡菜"));
		list.add(new KeyValue("8910","厚糕"));
		list.add(new KeyValue("8911","酱饼"));
		list.add(new KeyValue("8912","苹果炒牛肉片"));
		list.add(new KeyValue("8913","萝卜饭鳕鱼汤"));
		list.add(new KeyValue("8914","凤凰紫菜卷"));
		list.add(new KeyValue("8915","韩药参鸡汤"));
		list.add(new KeyValue("8916","蛤蜊粥"));
		list.add(new KeyValue("8917","甜辣茄饼"));
		list.add(new KeyValue("8918","墨鱼辣酱拌幼面"));
		list.add(new KeyValue("8919","海味泡菜"));
		list.add(new KeyValue("8920","红油鹌鹑"));
		list.add(new KeyValue("8921","纸包柠檬烤鱼"));
		list.add(new KeyValue("8922","口蘑烩全狗"));
		list.add(new KeyValue("8923","烤河珍"));
		list.add(new KeyValue("8924","青椒榨菜泡菜"));
		list.add(new KeyValue("8925","沙拉龙虾"));
		list.add(new KeyValue("8926","牛蒡花生酱串"));
		list.add(new KeyValue("8927","高丽菜结炖豆腐"));
		list.add(new KeyValue("8928","酱蟹"));
		list.add(new KeyValue("8929","韩风韩式拌饭"));
		list.add(new KeyValue("8930","黄豆芽泡菜"));
		list.add(new KeyValue("8931","韩国烤肉饭"));
		list.add(new KeyValue("8932","韩式泡菜烩饭"));
		list.add(new KeyValue("8933","安东刀切面"));
		list.add(new KeyValue("8934","拌蛤蜊"));
		list.add(new KeyValue("8935","鱿鱼粉肠"));
		list.add(new KeyValue("8936","牡蛎年糕汤"));
		list.add(new KeyValue("8937","韩国切片泡菜"));
		list.add(new KeyValue("8938","牛肉冷汤面"));
		list.add(new KeyValue("8939","韩国蒜茸泡菜"));
		list.add(new KeyValue("8940","豆腐荤杂烩"));
		list.add(new KeyValue("8941","全州拌饭"));
		list.add(new KeyValue("8942","大酱汤"));
		list.add(new KeyValue("8943","韩国炒年糕"));
		list.add(new KeyValue("8944","荠菜大酱汤"));
		list.add(new KeyValue("8945","韩国烤肉"));
		list.add(new KeyValue("8946","韩式辣酱拌饭"));
		list.add(new KeyValue("8947","烤明虾"));
		list.add(new KeyValue("8948","韩国药参鸡汤"));
		list.add(new KeyValue("8949","韩式辣炒年糕"));
		list.add(new KeyValue("8950","泥鳅鱼汤"));
		Country_food_Map.put("24",list);
	}

	private static String addQueryParams(Map<String, String> queryParams) {
		StringBuffer returnString = new StringBuffer(getDetailUrl);
		for (Map.Entry<String, String> entry : queryParams.entrySet())
			if (returnString.lastIndexOf("?") == -1) {
				returnString.append("?" + entry.getKey() + "=" + entry.getValue());
			} else {
				returnString.append("&" + entry.getKey() + "=" + entry.getValue());
			}
		return returnString.toString();
	}

	private static Document getDocument(String url) {
		try {
			return Jsoup.connect(url).header("Referer", Referer).header("Host", Host)
					.header("User-Agent", "Mozilla/5.0 (Windows; U; Windows NT 5.2) Gecko/2008070208 Firefox/3.0.1")
					.header("Accept", "text ml,application/xhtml+xml").header("Accept-Language", "zh-cn,zh;q=0.5")
					.header("Accept-Charset", "GB2312,utf-8;q=0.7,*;q=0.7").header("Connection", "keep-alive")
					.header("Cache-Control", "max-age=0").header("Accept-Encoding", "gzip, deflate").get();
		} catch (IOException e) {

			e.printStackTrace();
			return getDocument(url);
		}
	}

	public static Map<String, List<KeyValue>> getList(String id, String page) {
		List<KeyValue> list = new ArrayList<JapanKoreaFoodUtil.KeyValue>();
		Document doc = null;
		try {
			doc = getDocument(getFoodListUrl + "?id=" + id + "&page=" + page);
		} catch (Exception e) {
			e.printStackTrace();
			getList(id,page);
		}
		Elements element = doc.select("table");
		Elements hrefsElements = element.get(0).select("a");
		int count = 0;// 总共抓取的话有1810，太多了。这里限制每个地区最多每个15条记录
		for (Element href : hrefsElements) {
			if (count == 15) {
				//				break;
			}
			list.add(new KeyValue(href.attr("href").substring("windows.asp?id=".length()), href.text()));
			count++;
		}

		if(Country_food_Map.containsKey(id)){
			Country_food_Map.get(id).addAll(list);
		}else {
			Country_food_Map.put(id, list);
		}
		
		return Country_food_Map;
	}


	static class KeyValue {
		private String value;
		private String key;

		public KeyValue(String key, String value) {
			this.key = key;
			this.value = value;
		}

		public String getValue() {
			return value;
		}

		public void setValue(String value) {
			this.value = value;
		}

		public String getKey() {
			return key;
		}

		public void setKey(String key) {
			this.key = key;
		}

		@Override
		public String toString() {
			return "key -> " + key + " , value -> " + value;
		}
	}

	public static String getDetail(String id) {
		Map<String, String> queryParams = new HashMap<String, String>();
		queryParams.put("id", id);
		Document doc;
		try {
			doc = getDocument(addQueryParams(queryParams));
			Element element = doc.getElementById("table1").select("tr").get(1).select("td").last();
			return element.outerHtml().replace("&#65533;",", ");
		} catch (Exception e) {
			e.printStackTrace();
			return getDetail(id);
		}

	}

	public static void main(String[] args) {
//		getList("23", "1");
//		for (int page = 1; page <= 3; page++) {
//			getList("24", String.valueOf(page));
//		}
//		System.out.println(Country_food_Map);
	
//				for (Map.Entry<String, List<KeyValue>> entry : Country_food_Map
//						.entrySet()) {
//					// System.out.println("area_custom_map.put(\""+entry.getKey()+"\"");
//					System.out.println("list = new ArrayList<KeyValue>();");
//					for (KeyValue keyValue : entry.getValue()) {
//						System.out.println("list.add(new KeyValue(\""
//								+ keyValue.getKey() + "\",\"" + keyValue.getValue()
//								+ "\"));");
//					}
//					System.out.println("Country_food_Map.put(\"" + entry.getKey()
//							+ "\",list);");
//				}
		
				for (Map.Entry<String, List<KeyValue>> entry : Country_food_Map
						.entrySet()) {
					for (final KeyValue keyValue : entry.getValue()) {
						new Thread() {
							public void run() {
								CommonUtil.WriteFile(
										CommonUtil.writeFileBasePath
												+ "\\日韩料理查询\\content_"
												+ keyValue.getKey() + ".txt",
										getDetail(keyValue.getKey()));
							};
						}.start();
		
					}
				}

	}
}