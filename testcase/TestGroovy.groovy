package d.groovy;

import org.junit.Test;

import net.sf.json.groovy.JsonSlurper



public class TestGroovy {
	
	@Test
	public void test3() {
		
		assert 1 == 1
		assert 1 == 2 : "One is not two"
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
