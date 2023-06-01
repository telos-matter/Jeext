package controllers;

import java.lang.reflect.Constructor;

import jeext.controller.core.param.annotations.MID;
import jeext.controller.core.param.annotations.composer.ComposeWith;
import jeext.model.Model;

public class TestModel extends Model <TestModel> {

	@MID
	public Integer id;
	
	public String name;
	
	@ComposeWith(value = "kid")
	public Integer age;
	
	public long pchiw;
	
	@Override
	public Object getId() {
		return null;
	}

	public void setAge (Integer age) {
		System.out.println("in setter with +" +age);
		this.age = age.intValue();
	}
	
	@Override
	public String toString() {
		return "TestModel [name=" + name + ", age=" + age + ", pchiw=" + pchiw + "]";
	}

	public static void main(String[] args) {
//		Constructor <?> c =  TestModel.class.getDeclaredConstructors()[0];
//		System.out.println(c);
//		c.ins
	}
	
}
