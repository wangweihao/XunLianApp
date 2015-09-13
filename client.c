/*
 * =====================================================================================
 *
 *       Filename:  client.c
 *
 *    Description:  
 *
 *
 *        Version:  1.0
 *        Created:  2015年06月25日 00时05分00秒
 *       Revision:  none
 *       Compiler:  gcc
 *
 *         Author:   (wangweihao), 578867817@qq.com
 *        Company:  xiyoulinuxgroup
 *
 * =====================================================================================
 */

#include <stdio.h>
#include <stdlib.h>
#include <assert.h>
#include <string.h>
#include <sys/socket.h>
#include <sys/types.h>
#include <arpa/inet.h>
#include <unistd.h>

int main(int argc, char *argv[])
{
    if(argc < 2){
        printf("argument error\n");
        exit(1);
    }
	struct sockaddr_in server;
    bzero(&server, sizeof(server));
    server.sin_port = htons((int)atoi(argv[2]));
    server.sin_family = AF_INET;
    inet_pton(AF_INET, argv[1], &server.sin_addr);

    int sock_fd = socket(AF_INET, SOCK_STREAM, 0);
    if(sock_fd < 0){
        printf("create sockfd error\n");
        exit(1);
    }
    socklen_t len = sizeof(server);
    printf("connect...\n");
    int ret = connect(sock_fd, (struct sockaddr*)&server, len);
    if(ret == -1){
        printf("connect error\n");
        exit(1);
    }else{
        printf("connect success\n");
    }
    char buffer[1024];
    //while(1){
        bzero(buffer, 1024);
        //scanf("%s", buffer);
        //mark1 检测帐号是否存在 yes
        strcpy(buffer, "{\"mark\":1,\"account\":\"wangweihao\"}");
        //mark2 帐号注册 yes
        //strcpy(buffer, "{\"mark\":2,\"account\":\"10101010\", \"secret\":\"123123123\"}");
        //mark3 验证密宝 yes
        //strcpy(buffer, "{\"mark\":3,\"account\":\"1111\", \"type\":\"1\", \"verify\":\"188292929\"}");
        //mark4 修改密码 yes
        //strcpy(buffer, "{\"mark\":4,\"account\":\"1111\", \"secret\":\"weihao\"}");
        //mark5 修改资料 yes
        //strcpy(buffer, "{\"mark\":5,\"account\":\"wangweihao\",\"name\":\"hahahahaha\", \"head\":\"*********\"}");
        //mark6 需要增加标记，看是更新联系还是插入联系
        //2是Update 1是Insert yes
        //注意返回为空也算失败
        //update数据，并且更新isUpdate数值
        //strcpy(buffer, "{\"mark\":6,\"account\":\"3333\", \"type\":4, \"isUpdateOrInsert\":2, \"contact\":\"18829292929\"}");
        //mark7 本地没有数据，获得所有的数据 yes
        //strcpy(buffer, "{\"mark\":7,\"account\":\"2222\"}");
        //mark8 本地有数据，获得需要更新的数据 yes
        //strcpy(buffer, "{\"mark\":8,\"account\":\"2222\"}");
        //mark9 yes
        //strcpy(buffer, "{\"mark\":9, \"account\":\"2222\", \"friendaccount\":\"5555\"}");
        //mark10 借用mark11生成的二维码的信息组成新的json请求
        //strcpy(buffer, "{\"mark\":10, \"account\":\"3333\", \"friendaccount\":\"5555\", \"authority\":127, \"time_out\":7}");
        //mark11 yes 新建二维码
        //strcpy(buffer, "{\"mark\":11, \"account\":\"2222\", \"authority\":\"20\", \"time_out\":\"30\"}");
        //mark12    
        //strcpy(buffer, "{\"mark\":12,\"account\":\"2222\", \"secret\":\"2222\"}");
        //mark13
        //strcpy(buffer, "{\"mark\":13,\"account\":\"5555\", \"friendaccount\":\"3333\"}");
        send(sock_fd, buffer, 1024, 0);
        char buf[10000];
        recv(sock_fd, buf, 10000, 0);
        printf("%s\n", buf);
    //}



	return EXIT_SUCCESS;
}


