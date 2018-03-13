package ninja.mspp.tools;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * plug-in tools
 */
public class PluginTool {
	/**
	 * execute method
	 * @param plugin plugin
	 * @param clazz class
	 * @param args arguments
	 * @return return value
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 */
	public static < A extends Annotation > Object invoke( Object plugin, Class< A > clazz, Object... args )
			throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		Object ret = null;
		Method[] methods = plugin.getClass().getDeclaredMethods();
		for( Method method : methods ) {
			A annotation = method.getAnnotation( clazz );
			if( annotation != null ) {
				ret = method.invoke( plugin, args );
			}
		}
		return ret;
	}

	/**
	 * gets the boolean value
	 * @param plugin plugin
	 * @param clazz
	 * @param args
	 * @return
	 */
	public static < A extends Annotation > boolean getBoolean(
			Object plugin, Class< A > clazz, boolean defaultValue, Object... args
	) {
		boolean value = defaultValue;
		try {
			Object ret = PluginTool.invoke( plugin,  clazz, args );
			if( ret == null ) {
				value = defaultValue;
			}
			else {
				value = (Boolean)ret;
			}
		}
		catch( Exception e ) {
			e.printStackTrace();
		}

		return value;
	}
}
