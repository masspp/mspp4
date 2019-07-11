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
 * @since 2018-06-01 05:51:10+09:00
 *
 * Copyright (c) 2018, Mass++ Users Group
 * All rights reserved.
 */
package ninja.mspp.plugin.viewer.heatmap;

import java.util.ArrayList;

import ninja.mspp.annotation.method.MenuAction;
import ninja.mspp.annotation.method.MenuPosition;
import ninja.mspp.annotation.type.Plugin;
import ninja.mspp.model.dataobject.SampleObject;
import ninja.mspp.model.dataobject.SpectrumObject;
import ninja.mspp.model.gui.MenuNode;

@Plugin( "heatmap viewer" )
public class HeatMapViewer {
	private MenuNode menu;

	/**
	 * constructor
	 */
	public HeatMapViewer() {
		this.menu = MenuNode.TOOLS_MENU.item( "Heatmap" );
	}

	@MenuPosition
	public MenuNode getPosition() {
		return this.menu;
	}

	@MenuAction
	public void action() {
/*
		MsppManager manager = MsppManager.getInstance();
		SampleObject sample = manager.getSample( 0 );

		if( sample == null ) {
			return;
		}

		this.openHeatmap( sample );
*/
	}

	/**
	 * opens heatmap
	 * @param sample sample
	 */
	protected void openHeatmap( SampleObject sample ) {
		ArrayList< SpectrumObject > spectra = this.getSpectra( sample );
		if( spectra.size() == 0 ) {
			return;
		}
/*
		GuiManager gui = GuiManager.getInstance();
		MainFrame mainFrame = gui.getMainFrame();

		Heatmap heatmap = new Heatmap( spectra );
		HeatmapCanvas canvas = new HeatmapCanvas( heatmap, "RT", "m/z" );
		mainFrame.addMapWindow( "Heatmap",  canvas );
*/
	}

	/**
	 * gets the spectra
	 * @param sample sample
	 * @return spectra
	 */
	protected ArrayList< SpectrumObject > getSpectra( SampleObject sample ) {
		ArrayList< SpectrumObject > spectra = new ArrayList< SpectrumObject >();

		for( SpectrumObject spectrum : sample.getSpectra() ) {
			if( spectrum.getMsStage() > 1 && spectrum.getRt() > 0.0 ) {
				spectra.add( spectrum );
			}
		}

		return spectra;
	}


}
