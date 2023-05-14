package jeext;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import jeext.controller.core.Path;
import jeext.controller.core.util.JMap;

public class Test {

	public static void main(String[] args) throws InterruptedException {
//		List<Path> l = new ArrayList<>();
//		
//		for (int i = 0; i < 500_000; i++) {
//			l.add(new Path("" +i));
//		}
//		
//		long start = System.currentTimeMillis();
////		boolean contains = l.contains(new Path("999"));
//		l.get(l.indexOf(new Path("499999")));
//		
////		Thread.sleep(50);
//		long finish = System.currentTimeMillis();
//		
//		System.out.println(" +contains + in: " +(finish -start) +" ms");
	
		Map<String, Integer> l = new JMap<String, Integer>();
		l.put("10", 69);
		System.out.println(l);
		System.out.println(l.getClass());
		l = Collections.unmodifiableMap(l);
		System.out.println(l);
		System.out.println(l.getClass());
		System.out.println("Suiii");
		
	}
	
	
	

}
