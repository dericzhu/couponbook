package d.groovy;

import org.junit.Test;

import net.sf.json.groovy.JsonSlurper



public class TestGroovy {
	
	
	@Test
	public void test8() {
		Closure n 
		println null == n 
		
		
		Closure c = {println 'initiate c'}
		if(c) {
			println 'iamtrue'
		}else {
			println 'iamfalse'
		}
		
		
		
	}
	
	@Test
	public void test7() {
		def list = [1,2,3,5,7,9]
		list.each { println (it % 2) }
		
		
	}

	@Test
	public void test6() {
		for ( n in 0..10)
			println n

		for ( x in ["apple", "orange", "pear"])
			println x

		def hello = "Hello, World!"
		for ( c in hello)
			println c
	}

	@Test
	public void test5() {
		def array = [5, 8, 9, 1, 0]

		def arraySort = 	array.sort()
		arraySort.each { println it}
	}

	@Test
	public void test4() {
		greet('iamderic')
		println greet('iamderic')
	}

	def greet(greeting) {
		println greeting + ", World!"
	}


	@Test
	public void test3() {

		/*assert 1 == 1
		 assert 1 == 2 : "One is not two"*/

		def map = [a:'a',b:'b',c:[d:'d',e:['f', 'g']]]
		assert map.c.e[1] == 'h'
	}


	@Test
	public void test2() {
		def slurper = new JsonSlurper()
		def result = slurper.parseText('{"person":{"name":"Guillaume","age":33,"pets":["dog","cat"]}}')

		assert result.person.name == "Guillaume"
		assert result.person.age == 33
		assert result.person.pets.size() == 2
		assert result.person.pets[0] == "dog"
		assert result.person.pets[1] == "cat"
	}


	@Test
	public void test1() {
		println("iamdericgroovy");
	}
}
