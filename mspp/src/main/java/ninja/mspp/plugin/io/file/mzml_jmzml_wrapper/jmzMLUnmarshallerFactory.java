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
 * @author Masaki Murase
 * @since 2018-05-30 02:24:12+09:00
 *
 * Copyright (c) 2018, Mass++ Users Group
 * All rights reserved.
 */

package ninja.mspp.plugin.io.file.mzml_jmzml_wrapper;

import java.util.HashMap;
import java.util.Map.Entry;
import java.io.File;

import uk.ac.ebi.jmzml.xml.io.MzMLUnmarshaller;
import uk.ac.ebi.jmzml.xml.io.MzMLUnmarshallerException;

/**
 *
 * @author masakimu
 * 
 * Singleton class which manage hash pool of generated mzMLUnmarshaller instance.
 * 
 */
public class jmzMLUnmarshallerFactory {
    
    private HashMap<String, MzMLUnmarshaller> pool = new HashMap<>();
    
    private jmzMLUnmarshallerFactory(){};
    
    public static jmzMLUnmarshallerFactory getInstance(){
        return SingletonHolder.INSTANCE;
    }
    
    private static class SingletonHolder {
        private static final jmzMLUnmarshallerFactory INSTANCE = new jmzMLUnmarshallerFactory();
    }
    
    public synchronized MzMLUnmarshaller getUnmarshaller(File file){
        String fullpath = file.getAbsolutePath();
        MzMLUnmarshaller unmarshaller = this.pool.get(""+fullpath);
        
        if(unmarshaller ==null){
            unmarshaller = new MzMLUnmarshaller(file);
            this.pool.put(fullpath, unmarshaller);
        }         
        return unmarshaller;
    }
    
    public synchronized void removeUnmarshaller(File file){
        this.pool.remove(file.getAbsolutePath());
    }
    
    public synchronized void clearAllUnmarshaller(){
        this.pool.clear();
    }
    
}

