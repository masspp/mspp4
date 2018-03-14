package ninja.mspp.model;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 * plug-in method
 */
public class PluginMethod< A extends Annotation >  {
	private Method method;
	private Object plugin;
	private A annotation;

	/**
	 * constructor
	 * @param plugin plug-in
	 * @param method method
	 */
	public PluginMethod( Object plugin, Method method ) {
		this( plugin, method, null );
	}

	/**
	 * constructor
	 * @param plugin plug-in
	 * @param method method
	 * @param annotation annotation
	 */
	public PluginMethod( Object plugin, Method method, A annotation ) {
		this.plugin = plugin;
		this.method = method;
		this.annotation = annotation;
	}

	/**
	 * gets the plug-in
	 * @return plug-in
	 */
	public Object getPlugin() {
		return this.plugin;
	}

	/**
	 * gets the method
	 * @return method
	 */
	public Method getMethod() {
		return this.method;
	}

	/**
	 * gets the annotation
	 * @return annotation
	 */
	public A getAnnotation() {
		return this.annotation;
	}
}
