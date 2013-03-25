package com.opower.connectionpool;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.easymock.EasyMock;

/**
 * Provides a class for testing simple wrapper classes.  This implementation assumes that the 
 * wrapper class simply delegates all method calls to the wrapped class.  As such it attempts to
 * call every public method exposed by the wrapped class on the wrapper class and ensures that the 
 * underlying method on the wrapped class was called as a result. 
 * 
 * An example of its use is as follows:
 * 
 * {@code
 * WrapperTester wrapperTester = new WrapperTester(WrappedClass.class);
 * WrappedClass wrappedClass = (WrappedClass)wrapperTester.getWrappedValue();
 * WrapperClass wrapperClass = new WrapperClass(wrappedClass);
 * wrapperTester.run(wrappedClass);
 * }
 * 
 * @author Joshua Mark Rutherford
 */
public class WrapperTester {

	/**
	 * Initializes a new wrapper class tester.
	 * @param wrappedClass The class of the wrapped type.
	 */
	public WrapperTester(Class<?> wrappedClass) {
		this.wrappedClass = wrappedClass;
		this.wrappedValue = EasyMock.createMock(this.wrappedClass);
	}
	
	/**
	 * Gets the mock object that represents an instance of the wrapped class.
	 * @return A mock object that represents an instance of the wrapped class.
	 */
	public Object getWrappedValue() {
		return this.wrappedValue;
	}
	
	/**
	 * Runs tests that confirm every public method of the wrapped class, when called on the wrapper
	 * class are called on the wrapped class as well.
	 * @param wrapperValue An instance of a class that wraps the object returned by {@link #getWrappedValue() getWrappedValue}.
	 */
	public void run(Object wrapperValue) {
		
		Method[] methods = this.getMethods(this.wrappedClass);
		for (Method wrappedMethod : methods) {
			EasyMock.reset(this.wrappedValue);	
			Object[] parameterValues = this.getParameters(wrappedMethod);
			Object returnValue = this.getValue(wrappedMethod.getReturnType());
			try {
				if (wrappedMethod.getReturnType() == void.class) {
					EasyMock.expect(wrappedMethod.invoke(this.wrappedValue, parameterValues));
				} else {
					EasyMock.expect(wrappedMethod.invoke(this.wrappedValue, parameterValues)).andReturn(returnValue);
				}
			} catch (Exception e) {
				Assert.fail("Failed to invoke a the wrapped method " + wrappedMethod.getName() + " with parameters of type " + wrappedMethod.getParameterTypes().toString());
				e.printStackTrace();
			}
			EasyMock.replay(this.wrappedValue);
			try {
				Method wrapperMethod = wrapperValue.getClass().getMethod(wrappedMethod.getName(), wrappedMethod.getParameterTypes());
				try {
					wrapperMethod.invoke(wrapperValue, parameterValues);
				} catch (Exception e) {
					Assert.fail("Failed to invoke a the wrapper method " + wrappedMethod.getName() + " with parameters of type " + wrappedMethod.getParameterTypes().toString());
					e.printStackTrace();
				}
			} catch (Exception e) {
				Assert.fail("Failed to locate a the wrapper method " + wrappedMethod.getName() + " with parameters of type " + wrappedMethod.getParameterTypes().toString());
				e.printStackTrace();
			}
			EasyMock.verify(this.wrappedValue);
		}
	}
	
	private static final boolean BOOLEAN_VALUE = false;
	private static final byte BYTE_VALUE = 0;
	private static final char CHAR_VALUE = 0;
	private static final double DOUBLE_VALUE = 0;
	private static final float FLOAT_VALUE = 0;
	private static final int INT_VALUE = 0;
	private static final long LONG_VALUE = 0;
	private static final Object OBJECT_VALUE = null;
	private static final short SHORT_VALUE = 0;
	
	private Object wrappedValue;
	private Class<?> wrappedClass;
	
	/**
	 * Gets the public methods declared by a class.
	 * @param type The class for which the public methods are returned.
	 * @return The public methods declared by the class.
	 */
	private Method[] getMethods(Class<?> type) {
		List<Method> methods = new ArrayList<Method>(); 
		for (Method method : type.getDeclaredMethods()) {
			if (Modifier.isPublic(method.getModifiers())) {
				methods.add(method);
			}
		}
		return methods.toArray(new Method[0]);
	}
	
	/**
	 * Gets a parameter array suitable for use in the invocation of a method.
	 * @param method The method for which the parameter array is returned.
	 * @return A parameter array suitable for use in the invocation of the method.
	 */
	private Object[] getParameters(Method method) {
		Class<?>[] types = method.getParameterTypes();
		List<Object> values = new ArrayList<Object>(types.length);
		for (Class<?> type : types) {
			values.add(this.getValue(type));
		}
		return values.toArray();
	}
	
	/**
	 * Gets the value for a parameter or return.
	 * @param type The class for which the value is returned.
	 * @return The value for a parameter or return.
	 */
	private Object getValue(Class<?> type) {
		Object result = null;
		if (type == boolean.class) {
			result = WrapperTester.BOOLEAN_VALUE; 
		} else if (type == byte.class) {
			result = WrapperTester.BYTE_VALUE;
		} else if (type == char.class) {
			result = WrapperTester.CHAR_VALUE;
		} else if (type == double.class) {
			result = WrapperTester.DOUBLE_VALUE;
		} else if (type == float.class) {
			result = WrapperTester.FLOAT_VALUE;
		} else if (type == int.class) {
			result = WrapperTester.INT_VALUE;
		} else if (type == long.class) {
			result = WrapperTester.LONG_VALUE;
		} else if (type == short.class) {
			result = WrapperTester.SHORT_VALUE;
		} else if (type == void.class) {
			result = null;
		} else {
			result = type.cast(WrapperTester.OBJECT_VALUE);
		}
		return result;
	}
		
}
