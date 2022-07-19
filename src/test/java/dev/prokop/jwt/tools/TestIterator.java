package dev.prokop.jwt.tools;

import org.junit.Assert;
import org.junit.Test;

import java.util.Iterator;

import static dev.prokop.jwt.tools.Json.*;

public class TestIterator
{
	@Test
	public void testBoolean()
	{
		Json b1 = Json.make(true);
		Iterator<Json> iter = b1.iterator();
		Assert.assertNotNull(iter);
		Assert.assertEquals(true, iter.hasNext());
		Json val =iter.next();
		Assert.assertEquals(true, val.getValue());
		Assert.assertEquals(false, iter.hasNext());
	}

	@Test
	public void testNil()
	{
		Json nil = Json.nil();
		Iterator<Json> iterator = nil.iterator();
		Assert.assertNotNull(iterator);
		Assert.assertEquals(true, iterator.hasNext());
		Json next = iterator.next();
		Assert.assertNull(next.getValue());
		Assert.assertEquals(false, iterator.hasNext());
	}

	@Test
	public void testNumber()
	{
		Json n1 = Json.make(567);
		Iterator<Json> iter = n1.iterator();
		Assert.assertNotNull(iter);
		Assert.assertEquals(true, iter.hasNext());
		Json val = iter.next();
		Assert.assertEquals(567, val.getValue());
		Assert.assertEquals(false, iter.hasNext());
	}

	@Test
	public void testString()
	{
		Json s1 = Json.make("Hello");
		Iterator<Json> iter = s1.iterator();
		Assert.assertNotNull(iter);
		Assert.assertEquals(true, iter.hasNext());
		Json val = iter.next();
		Assert.assertEquals("Hello", val.getValue());
		Assert.assertEquals(false, iter.hasNext());
	}

	@Test
	public void testArray()
	{
		Json numbers = array(4,3,7);
		Iterator iter = numbers.iterator();
		Assert.assertNotNull(iter);
		Assert.assertEquals(true, iter.hasNext());
		Json val = (Json)iter.next();
		Assert.assertEquals(4, val.getValue());
		Assert.assertEquals(true, iter.hasNext());
		val = (Json)iter.next();
		Assert.assertEquals(3, val.getValue());
		Assert.assertEquals(true, iter.hasNext());
		val = (Json)iter.next();
		Assert.assertEquals(7, val.getValue());
		Assert.assertEquals(false, iter.hasNext());
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testObject()
	{
		Json o1 = object("p", 1, "p2", "p2value");
		System.out.println(o1);
		Iterator<Json> iter = o1.iterator();
		Assert.assertNotNull(iter);
		Assert.assertEquals(true, iter.hasNext());
		Json val = iter.next();
//		Assert.assertEquals("p", val.getKey());
		Assert.assertEquals(1, val.getValue());
		Assert.assertEquals(true, iter.hasNext());
		val = iter.next();
//		Assert.assertEquals("p2", val.getKey());
		Assert.assertEquals("p2value", val.getValue());
		Assert.assertEquals(false, iter.hasNext());
	}
}
