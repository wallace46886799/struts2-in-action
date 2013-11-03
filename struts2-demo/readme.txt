1.MDC or NDC
org.apache.log4j 
Class MDC
java.lang.Object
  org.apache.log4j.MDC
public class MDC
extends java.lang.Object
The MDC class is similar to the NDC class except that it is based on a map instead of a stack. It provides mapped diagnostic contexts. A Mapped Diagnostic Context, or MDC in short, is an instrument for distinguishing interleaved log output from different sources. Log output is typically interleaved when a server handles multiple clients near-simultaneously.
The MDC is managed on a per thread basis. A child thread automatically inherits a copy of the mapped diagnostic context of its parent.
The MDC class requires JDK 1.2 or above. Under JDK 1.1 the MDC will always return empty values but otherwise will not affect or harm your application.
 
 
Since:
1.2



NDC和MDC
NDC（Nested Diagnostic Context）和MDC（Mapped Diagnostic Context）是log4j种非常有用的两个类，它们用于存储应用程序的上下文信息（context infomation），从而便于在log中使用这些上下文信息。
 
NDC采用了一个类似栈的机制来push和pop上下文信息，每一个线程都独立地储存上下文信息。比如说一个servlet就可以针对每一个request创建对应的NDC，储存客户端地址等等信息。
当使用的时候，我们要尽可能确保在进入一个context的时候，把相关的信息使用NDC.push(message);在离开这个context的时候使用NDC.pop()将信息删除。另外由于设计上的一些问题，还需要保证在当前thread结束的时候使用NDC.remove()清除内存，否则会产生内存泄漏的问题。
存储了上下文信息之后，我们就可以在log的时候将信息输出。在相应的PatternLayout中使用”%x”来输出存储的上下文信息，下面是一个PatternLayout的例子：
%r [%t] %-5p %c{2} %x - %m%n
使用NDC最重要的好处就是，当我们想输出一些上下文的信息的时候，不需要让logger去寻找这些信息，而只需要在适当的位置进行存储，然后再配置文件中修改PatternLayout。在最新的log4j 1.3版本中增加了一个org.apache.log4j.filters.NDCMatchFilter，用来
根据NDC中存储的信息接受或拒绝一条log信息。
 
MDC和NDC非常相似，所不同的是MDC内部使用了类似map的机制来存储信息，上下文信息也是每个线程独立地储存，所不同的是信息都是以它们的key值存储在”map”中。相对应的方法，MDC.put(key, value); MDC.remove(key); MDC.get(key); 在配置PatternLayout的时候使用：%x{key}来输出对应的value。同样地，MDC也有一个org.apache.log4j.filters.MDCMatchFilter。这里需要注意的一点，MDC是线程独立的，但是一个子线程会自动获得一个父线程MDC的copy。
至于选择NDC还是MDC要看需要存储的上下文信息是堆栈式的还是key/value形式的。
 
动态修改日志配置
在开发过程中，我们经常会遇到修改log4j配置的情况，在这种情况下，频繁重启应用显然是不可接受的。幸好log4j提供了自动重新加载配置文件的能力，在配置文件修改后，便会自己重新加载配置。在1.2及以前的版本中DOMConfigurator和PropertyConfigurator都提供了configureAndWatch方法，对指定的配置文件进行监控，并且可以设置检查的间隔时间。