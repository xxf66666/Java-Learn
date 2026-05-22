package s04_inheritance;

/**
 * 父类：所有动物共有的属性和行为
 */
public class Animal {

    // protected = 本类 + 同包 + 子类可访问
    // 字段如果子类要直接用，常用 protected
    protected String name;

    public Animal(String name) {
        this.name = name;
    }

    /** 睡觉：所有动物都一样的实现 */
    public void sleep() {
        System.out.println(name + " 在睡觉 zzz");
    }

    /**
     * 发声：默认实现（通用），子类可以重写成自己的
     */
    public void sound() {
        System.out.println(name + " 发出某种声音");
    }
}
