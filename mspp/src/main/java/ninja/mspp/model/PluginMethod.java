/**
 * BSD 3-Clause License
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * * Neither the name of the copyright holder nor the names of its
 *   contributors may be used to endorse or promote products derived from
 *   this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 *
 * @author Mass++ Users Group
 * @author Satoshi Tanaka
 * @since 2018-03-13 18:14:26+09:00
 *
 * Copyright (c) 2018, Mass++ Users Group
 * All rights reserved.
 */
package ninja.mspp.model;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import ninja.mspp.ObjectManager;
import ninja.mspp.annotation.parameter.FxmlLoaderParam;
import ninja.mspp.annotation.parameter.MainFrameParam;

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

	/**
	 * invokes method
	 * @param args arguments
	 * @return return value
	 */
	public Object invoke( Object... args ) {
		ObjectManager manager = ObjectManager.getInstane();

		Object ret = null;
		try {
			Annotation[][] allAnnotations = this.method.getParameterAnnotations();
			List< Object > parameters = new ArrayList< Object >();
			int index = 0;

			for( Annotation[] annotations : allAnnotations ) {
				if( this.hasAnnotation( annotations, MainFrameParam.class ) ) {
					parameters.add( manager.getMainFrame() );
				}
				else if( this.hasAnnotation( annotations, FxmlLoaderParam.class ) ) {
					parameters.add( manager.getFxmlLoader() );
				}
				else {
					parameters.add( args[ index ] );
					index++;
				}
			}

			Object[] array = null;
			if( !parameters.isEmpty() ) {
				array = parameters.toArray( new Object[ parameters.size() ] );
			}
			ret = this.method.invoke( this.plugin, array );
		}
		catch( Exception e ) {
			e.printStackTrace();
		}
		return ret;
	}

	/**
	 * judges whether the array has specified annotation
	 * @param annotations annotations
	 * @param clazz annotations class
	 * @return
	 */
	private boolean hasAnnotation( Annotation[] annotations, Class< ? > clazz ) {
		boolean found = false;
		if( annotations == null || clazz == null ) {
			return found;
		}

		for( Annotation annotation : annotations ) {
			if( clazz.equals( annotation.annotationType() ) ) {
				found = true;
			}
		}
		return found;
	}
}
