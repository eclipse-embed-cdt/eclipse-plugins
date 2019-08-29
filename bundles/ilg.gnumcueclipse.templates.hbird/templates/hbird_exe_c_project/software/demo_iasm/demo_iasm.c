
#include <stdio.h>  

int main(void)
{

    printf("\n##############################################################\n");
    printf("##############################################################\n");
    printf("##############################################################\n");
    printf("##############################################################\n");
    printf("\nThis is a Demo to use the inline ASM to conduct ADD operations\n");
    printf("We will use the inline assembly 'add' instruction to add two operands 100 and 200\n");
    printf("The expected result is 300\n");
    printf("\n\nIf the result is 300, then we print PASS, otherwise FAIL\n");

    // 使用C语言声明3个变量
    int sum;
    int add1 = 100;
    int add2 = 200;

    //插入汇编代码调用add汇编指令进行加法操作
    __asm__ __volatile__(
    "add %[dest],%[src1],%[src2]"     //使用add指令，一个目标操作数（命名为dest），
                                // 两个源操作数（分别命名为src1和src2）。
    :[dest]"=r"(sum)                     //将add指令的目标操作数dest和C程序中的
                                    // sum变量绑定。
                                // "=r"约束表示
    :[src1]"r"(add1), [src2]"r"(add2) //将add指令的源操作数src1和C程序中的
                                // add1变量绑定；将源操作数src2和add2变
                                // 量绑定。
    //此内联汇编没有指定可能影响的寄存器或者存储器，因此省略第三个冒号。
    );  

// 上述代码使用了“%[字符]”的方式明确指定变量关系，如果使用“%数字”的方式进行隐含指定,则方式
//  如下：
//    "add %0,%1,%2"     //使用add指令，一个目标操作数（用%0指定第一个输出操作数sum），
//                        // 两个源操作数（分别用%1和%2指定第一个操作数add1和第二个
//                          // 输入操作数add2）。
//    :"=r"(sum)          // 只有一个输出操作数，由%0隐含指定
//                        // "=r"约束表示
//    :"r"(add1), "r"(add2) //有两个输出操作数，由%1和%2隐含指定
//                                

    //判断内联汇编是否正确算出结果，如果汇编指令add正确的执行并且将结果返回，那么sum变
    // 量应该等于300。
    if(sum == 300) {
        printf("!!! PASS !!!");
    }else{
        printf("!!! FAIL !!!");
    }

    return 0;
}


