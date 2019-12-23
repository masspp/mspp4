/*
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
 * @author Mass++ Users Group (https://www.mspp.ninja/)
 * @author Satoshi Tanaka
 * @since Tue Mar 13 18:14:26 JST 2018
 *
 * Copyright (c) 2018 Satoshi Tanaka
 * All rights reserved.
 */
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
			Object ret = PluginTool.invoke( plugin, clazz, args );
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
