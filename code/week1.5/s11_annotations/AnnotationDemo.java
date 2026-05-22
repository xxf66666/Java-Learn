package s11_annotations;

import java.lang.annotation.*;
import java.lang.reflect.*;
import java.util.HashMap;
import java.util.Map;

public class AnnotationDemo {

    // ====== 自定义注解 1：标"耗时方法"，贴在方法上 ======
    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @interface LogTime {
        String value() default "";        // 可选属性，默认空串
    }

    // ====== 自定义注解 2：标"该被容器管理"，贴在类上 ======
    @Target(ElementType.TYPE)
    @Retention(RetentionPolicy.RUNTIME)
    @interface MyBean {}

    // ====== 业务类 ======
    @MyBean
    public static class User {
        private String name;            // 私有字段（反射要 setAccessible）
        public int age;

        public User() {}
        public User(String name) { this.name = name; }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        @LogTime("打招呼")
        public String greet() {
            return "Hello, " + name;
        }
    }

    @MyBean
    public static class Order { }

    public static class NotBean { }     // 没标 @MyBean

    // ====== main ======
    public static void main(String[] args) throws Exception {
        getClassObject();
        reflectNewAndCall();
        reflectPrivateField();
        readAnnotation();
        miniIoc();
    }

    /** 拿 Class 对象的 3 种方式 */
    static void getClassObject() {
        System.out.println("\n=== 拿 Class ===");

        // 方式 1: 类名.class（最常用）
        Class<User> c1 = User.class;
        // 方式 2: 实例.getClass()
        User u = new User();
        Class<?> c2 = u.getClass();
        // 方式 3: Class.forName(全类名字符串)
        // 略

        System.out.println("c1 == c2 ? " + (c1 == c2));      // true（同一个 Class 对象）
        System.out.println("name: " + c1.getName());
        System.out.println("simpleName: " + c1.getSimpleName());
    }

    /** 反射 new 对象 + 调方法 */
    static void reflectNewAndCall() throws Exception {
        System.out.println("\n=== 反射 new + 调方法 ===");

        Class<User> c = User.class;

        // 反射调无参构造器
        Constructor<User> noArg = c.getDeclaredConstructor();
        User u1 = noArg.newInstance();

        // 反射调带参构造器：传参数类型 String.class
        Constructor<User> withArg = c.getDeclaredConstructor(String.class);
        User u2 = withArg.newInstance("Alice");
        System.out.println("u2.name = " + u2.getName());

        // 反射调方法：getDeclaredMethod(方法名, 参数类型...)
        Method m = c.getDeclaredMethod("setName", String.class);
        m.invoke(u1, "Bob");        // 在 u1 上调 setName("Bob")
        System.out.println("u1.name = " + u1.getName());

        // 调有返回值的方法
        Method greet = c.getDeclaredMethod("greet");
        Object result = greet.invoke(u1);
        System.out.println("greet() = " + result);
    }

    /** 反射访问私有字段 */
    static void reflectPrivateField() throws Exception {
        System.out.println("\n=== 反射读私有字段 ===");

        User u = new User("Carol");

        // getDeclaredField 包含 private（getField 只能拿 public）
        Field f = User.class.getDeclaredField("name");

        // 暴力打开 private 访问
        f.setAccessible(true);

        // 读
        Object value = f.get(u);
        System.out.println("name = " + value);

        // 写
        f.set(u, "Dave");
        System.out.println("改后 = " + u.getName());
    }

    /** 读注解 */
    static void readAnnotation() throws Exception {
        System.out.println("\n=== 读注解 ===");

        Method greet = User.class.getDeclaredMethod("greet");

        // 判断是否标了某注解
        if (greet.isAnnotationPresent(LogTime.class)) {
            // 拿到注解实例
            LogTime ann = greet.getAnnotation(LogTime.class);
            // 注解的属性是方法调用
            System.out.println("方法 " + greet.getName() + " 标了 @LogTime, value = " + ann.value());
        }
    }

    /** 迷你 IoC：扫描带 @MyBean 的类自动 new */
    static void miniIoc() throws Exception {
        System.out.println("\n=== 迷你 IoC ===");

        // 候选类（真实 Spring 会扫整个 classpath）
        Class<?>[] candidates = { User.class, Order.class, NotBean.class };

        // Bean 容器
        Map<Class<?>, Object> beans = new HashMap<>();

        for (Class<?> c : candidates) {
            // 只创建标了 @MyBean 的
            if (c.isAnnotationPresent(MyBean.class)) {
                Object bean = c.getDeclaredConstructor().newInstance();
                beans.put(c, bean);
                System.out.println("注册 Bean: " + c.getSimpleName());
            }
        }

        // 拿出来用
        User u = (User) beans.get(User.class);
        u.setName("Eve");
        System.out.println("从容器拿: " + u.getName());

        System.out.println("NotBean 在容器里? " + beans.containsKey(NotBean.class));
    }
}
