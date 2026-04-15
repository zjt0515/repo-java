## 编译器使用的注解

不会被编译进入class文件

- @override: 让编译器检查是否正确实现覆写
- @suppressWarnings: 告诉编译器忽略警告

## 处理.class文件使用的注解

部分工具在加载class时，对class做动态修改，实现一些特殊的功能。会被编译进入.class文件，但是加载结束后并不会存在于内存中。

## 程序运行期读取的注解

加载后一直存在于JVM中，这也是最常用的注解。

> 这也是最常用的注解。例如，一个配置了@PostConstruct的方法会在调用构造方法后自动被调用
> （这是Java代码读取该注解实现的功能，JVM并不会识别该注解）。

定义配置参数：配置参数必须是常量

- 所有基本类型
- String
- 枚举类型
- 上述类型以及Class的数组

```java
public class Hello {
    //三个参数
    @Check(min=0, max=100, value=55)
    public int n;
    //一个参数
    @Check(value=99)
    public int p;
    //省略写法
    @Check(99) // @Check(value=99)
    public int x;
    //所有参数使用默认值
    @Check
    public int y;
}
```
