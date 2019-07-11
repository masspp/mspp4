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
 * @since 2018-02-22 19:20:46+09:00
 *
 * Copyright (c) 2018, Mass++ Users Group
 * All rights reserved.
 */
package ninja.mspp;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.prefs.Preferences;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.stereotype.Component;

import ninja.mspp.annotation.type.Plugin;
import ninja.mspp.model.PluginMethod;
import ninja.mspp.model.PluginObject;
import ninja.mspp.view.SpringFXMLLoader;

/**
 * Mass++ manager (Singleton class)
 */
@Component
public class MsppManager implements Iterable< Object > {
	@Autowired
	private ApplicationContext context;

	@Autowired
	SpringFXMLLoader fxmlLoader;

	// instance
	private static MsppManager instance = null;

	// config properties
	private ResourceBundle config;

	// message properties
	private ResourceBundle messages;

	// plugins
	private List< Object > plugins;

	// preferences
	private Preferences preferences;

	/**
	 * constructor
	 */
	private MsppManager() {
	}

	/**
	 * initializes object
	 */
	public void initialize() {
		MsppManager.instance = this;

		this.config = ResourceBundle.getBundle( "config" );
		this.messages = ResourceBundle.getBundle( "messages" );
		this.plugins = this.createPluginArray();
		this.preferences = Preferences.userRoot().node( "mspp4/parameters" );
	}

	/**
	 * gets the config properties
	 * @return config properties
	 */
	public ResourceBundle getConfig() {
		return this.config;
	}

	/**
	 * gets the messages
	 * @return messages
	 */
	public ResourceBundle getMessages() {
		return this.messages;
	}

	public SpringFXMLLoader getFxmlLoader() {
		return fxmlLoader;
	}

	/**
	 * save string value
	 * @param name parameter name
	 * @param value parameter value
	 */
	public void saveString( String name, String value ) {
		this.preferences.put( name, value );
	}

	/**
	 * loads string value
	 * @param name parameter name
	 * @param defaultValue default value
	 * @return parameter value
	 */
	public String loadString( String name, String defaultValue ) {
		return this.preferences.get( name,  defaultValue );
	}

	// array list
	private List< Object > createPluginArray() {
		List< Object > array = this.searchPlugins();

		return array;
	}

	/**
	 * search plugins
	 * @param files files
	 * @return plugins
	 */
	private List< Object > searchPlugins() {
		List< Object > list = new ArrayList< Object >();
		ClassPathScanningCandidateComponentProvider provider = new ClassPathScanningCandidateComponentProvider( false );
		provider.addIncludeFilter( new AnnotationTypeFilter( Plugin.class ) );
		Set< BeanDefinition > beans = provider.findCandidateComponents( "ninja.mspp" );

		Map< Object, Integer > orderMap = new HashMap< Object, Integer >();;
		for( BeanDefinition bean : beans ) {
			Object object = null;
			try {
				Class< ? > clazz = Class.forName( bean.getBeanClassName() );
				Plugin annotation = clazz.getDeclaredAnnotation( Plugin.class );
				try {
					object = this.context.getBean( clazz );
				}
				catch( Exception e1 ) {
					try {
						object = clazz.newInstance();
					}
					catch( Exception e2 ) {
						e1.printStackTrace();
						e2.printStackTrace();
					}
				}
				if( object != null ) {
					orderMap.put( object,  annotation.order() );
					list.add( object );
				}
			}
			catch( Exception e ) {
				e.printStackTrace();
			}
		}

		list.sort(
			( object1, object2 ) -> {
				int order1 = orderMap.get( object1 );
				int order2 = orderMap.get( object2 );
				return ( order1 - order2 );
			}
		);
		return list;
	}

	/**
	 * gets specified plug-ins
	 * @param clazz annotation class
	 * @return plug-in array
	 */
	public < A extends Annotation > List< PluginObject< A > > getPlugins( Class< A > clazz ) {
		ArrayList< PluginObject< A > > array = new ArrayList< PluginObject< A > >();
		for( Object plugin : this.plugins ) {
			A annotation = plugin.getClass().getAnnotation( clazz );
			if( annotation != null ) {
				PluginObject< A > object = new PluginObject< A >( plugin, annotation );
				array.add( object );
			}
		}

		return array;
	}

	/**
	 * gets plug-in methods
	 * @param clazz class
	 * @return plug-in methods
	 */
	public < A extends Annotation > List< PluginMethod< A > > getMethods( Class< A > clazz ) {
		ArrayList< PluginMethod< A > > array = new ArrayList< PluginMethod< A > >();
		for( Object plugin : this.plugins ) {
			Method[] methods = plugin.getClass().getDeclaredMethods();
			for( Method method : methods ) {
				A annotation = method.getAnnotation( clazz );
				if( annotation != null ) {
					PluginMethod< A > pluginMethod = new PluginMethod< A >( plugin, method, annotation );
					array.add( pluginMethod );
				}
			}
		}

		return array;
	}

	/**
	 * invokes methods
	 * @return
	 */
	public < A extends Annotation > void invokeAll( Class< A > clazz, Object... args ) {
		List< PluginMethod< A > > methods = this.getMethods( clazz );
		for( PluginMethod< A > method : methods ) {
			method.invoke( args );
		}
	}

	@Override
	public Iterator<Object> iterator() {
		return this.plugins.iterator();
	}

	/**
	 * gets the instance
	 * @return MsppManager instance (This is the only object.)
	 */
	public static MsppManager getInstance() {
		return MsppManager.instance;
	}
}
