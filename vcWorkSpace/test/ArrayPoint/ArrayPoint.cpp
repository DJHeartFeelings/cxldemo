// ArrayPoint.cpp : Defines the entry point for the console application.
//

#include "stdafx.h"
//ע��ָ�����������ָ��ֱ������ָ���ά�����
#include <stdio.h>

main()
{
    static int m[3][4]={0,1,2,3,4,5,6,7,8,9,10,11};/* �����ά����m����ʼ��*/ 
    int (*p)[4];//����ָ��  p��ָ�룬ָ��һά����,ÿ��һά������4��intԪ��
    int i,j;
    int *q[3];//ָ������ q�����飬����Ԫ����ָ�룬3��intָ��
    p=m;    //p��ָ�룬����ֱ��ָ���ά����
    printf("--����ָ�����Ԫ��--\n");
    for(i=0;i<3;i++)/*�����ά�����и���Ԫ�ص���ֵ*/
    {
        for(j=0;j<4;j++) 
        {
            printf("%3d ",*(*(p+i)+j));
        }
        printf("\n");
    }
    printf("\n");
    for(i=0;i<3;i++,p++)//p�ɿ�������ָ��
    {
        printf("%3d ",**p);//ÿһ�еĵ�һ��Ԫ��
        printf("%3d ",*(*p+1));//ÿһ�еĵڶ���Ԫ��
        printf("%3d ",*(*p+2));//ÿһ�еĵ�����Ԫ��
        printf("%3d ",*(*p+3));//ÿһ�еĵ��ĸ�Ԫ��
        printf("\n");
    }
    printf("\n");
    printf("--ָ���������Ԫ��--\n");
    for(i=0;i<3;i++)
        q[i]=m[i];//q�����飬Ԫ��q[i]��ָ��
    for(i=0;i<3;i++)
    {
        for(j=0;j<4;j++)
        {
            printf("%3d ",q[i][j]);//q[i][j]�ɻ���*(q[i]+j)
        }
        printf("\n");
    }
    printf("\n");
    q[0]=m[0];
    for(i=0;i<3;i++)
    {
        for(j=0;j<4;j++)
        {
            printf("%3d ",*(q[0]+j+4*i));
        }
        printf("\n");
    }
    printf("\n");
    
}
