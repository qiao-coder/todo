package com.tufei.todo

/**
 * @author tufei
 * @date 2018/1/4.
 */
interface BaseView<T> {
    //继承这个接口的类，如果不是抽象类，必须声明这个presenter成员变量
    var presenter: T
}