package annotations;

// 注解相关元注解
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

// 反射相关类
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Week 1.5 §07 配套示例：自定义注解 + 反射
 *
 * 跑这个 main 看每段：
 *  1. 反射拿 Class 对象
 *  2. 反射 new 对象 + 调方法
 *  3. 反射读私有字段
 *  4. 读自定义注解
 *  5. "迷你 IoC" 演示
 */
public class AnnotationReflectionDemo {

    // ============ 自定义注解 ============

    /** 标记"耗时方法" */
    @Target(ElementType.METHOD)               // 只能贴在方法上
    @Retention(RetentionPolicy.RUNTIME)       // 运行时保留，反射能读
    @interface LogTime {
        String value() default "";             // 可选属性，默认空串
        int level() default 1;                  // 第二个属性
    }

    /** 标记"该被容器管理"（迷你 @Component） */
    @Target(ElementType.TYPE)                  // 贴在类上
    @Retention(RetentionPolicy.RUNTIME)
    @interface MyBean {}

    // ============ 被测试的类 ============

    @MyBean
    public static class User {
        private String name;        // 私有字段
        public int age;             // 公有字段

        public User() {}
        public User(String name) { this.name = name; }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        @LogTime(value = "招呼", level = 2)
        public String greet() {
            return "Hello, " + name;
        }
    }

    @MyBean
    public static class Order {
        public String orderNo;
    }

    public static class NotBean {              // 没标 @MyBean
    }

    // ============ main ============

    public static void main(String[] args) throws Exception {
        getClassObject();
        reflectInstantiate();
        reflectPrivateField();
        readAnnotation();
        miniIoc();
    }

    /** 取 Class 对象的 3 种方式 */
    static void getClassObject() {
        System.out.println("\n=== 取 Class ===");

        // 1) 类名.class
        Class<User> c1 = User.class;
        // 2) 实例.getClass()
        User u = new User();
        Class<?> c2 = u.getClass();
        // 3) Class.forName("全类名")
        try {
            Class<?> c3 = Class.forName(User.class.getName());
            System.out.println(c1 == c2);          // true（同一个 Class 对象）
            System.out.println(c1 == c3);          // true
            System.out.println("Class 名 = " + c1.getName());
            System.out.println("简单名 = " + c1.getSimpleName());
            System.out.println("父类 = " + c1.getSuperclass().getName());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /** 反射 new 对象 + 调方法 */
    static void reflectInstantiate() throws Exception {
        System.out.println("\n=== 反射 new + 调方法 ===");

        Class<User> c = User.class;

        // 拿无参构造器：getDeclaredConstructor(参数类型...)
        Constructor<User> noArg = c.getDeclaredConstructor();
        User u1 = noArg.newInstance();
        System.out.println("无参 new: " + u1.getName());

        // 拿带参构造器
        Constructor<User> withArg = c.getDeclaredConstructor(String.class);
        User u2 = withArg.newInstance("Alice");
        System.out.println("带参 new: " + u2.getName());

        // 反射调方法：getDeclaredMethod(方法名, 参数类型...)
        Method m = c.getDeclaredMethod("setName", String.class);
        // invoke(对象, 参数...)：在指定对象上调方法
        m.invoke(u1, "Bob");
        System.out.println("setName 后: " + u1.getName());

        // 调返回值方法
        Method greet = c.getDeclaredMethod("greet");
        String result = (String) greet.invoke(u1);
        System.out.println("greet() = " + result);
    }

    /** 反射访问私有字段 */
    static void reflectPrivateField() throws Exception {
        System.out.println("\n=== 反射读私有字段 ===");

        User u = new User("Carol");

        Class<?> c = User.class;
        // getDeclaredField 包含私有字段（getField 只能拿 public）
        Field nameField = c.getDeclaredField("name");

        // 暴力访问：跳过 private 检查
        nameField.setAccessible(true);

        // 读
        String name = (String) nameField.get(u);
        System.out.println("读 name = " + name);

        // 写
        nameField.set(u, "Dave");
        System.out.println("改后 = " + u.getName());
    }

    /** 读取自定义注解 */
    static void readAnnotation() throws Exception {
        System.out.println("\n=== 读注解 ===");

        Method greet = User.class.getDeclaredMethod("greet");

        // isAnnotationPresent 判断是否标了某注解
        if (greet.isAnnotationPresent(LogTime.class)) {
            // getAnnotation 拿到注解实例
            LogTime ann = greet.getAnnotation(LogTime.class);
            // 注解的属性是方法调用
            System.out.println("value = " + ann.value());
            System.out.println("level = " + ann.level());
        }
    }

    /** 迷你 IoC：扫描带 @MyBean 的类，自动 new 出来 */
    static void miniIoc() throws Exception {
        System.out.println("\n=== 迷你 IoC ===");

        // 待"扫描"的类列表（真实 Spring 会扫整个 classpath）
        Class<?>[] candidates = { User.class, Order.class, NotBean.class };

        // Bean 容器
        var beans = new java.util.HashMap<Class<?>, Object>();

        for (Class<?> c : candidates) {
            // 只创建标了 @MyBean 的
            if (c.isAnnotationPresent(MyBean.class)) {
                // 反射调无参构造器
                Object bean = c.getDeclaredConstructor().newInstance();
                beans.put(c, bean);
                System.out.println("注册 Bean: " + c.getSimpleName());
            }
        }

        // 现在 beans 里有 User 和 Order
        User u = (User) beans.get(User.class);
        u.setName("Eve");
        System.out.println("拿出来用: " + u.getName());

        System.out.println("NotBean 在容器里? " + beans.containsKey(NotBean.class));
    }
}
