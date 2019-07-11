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
package ninja.mspp.plugin.viewer.overlap;

import ninja.mspp.annotation.method.MenuAction;
import ninja.mspp.annotation.method.MenuCheck;
import ninja.mspp.annotation.method.MenuPosition;
import ninja.mspp.annotation.method.OpenSpectrum;
import ninja.mspp.annotation.type.Plugin;
import ninja.mspp.model.dataobject.SpectrumObject;
import ninja.mspp.model.dataobject.XYData;
import ninja.mspp.model.gui.MenuNode;

@Plugin( "overlap canvas")
public class OverlapViewer {
	/** canvas */
	private static OverlapCanvas canvas = null;

	/** menu */
	MenuNode menu;

	/**
	 * constructor
	 */
	public OverlapViewer() {
		this.menu = MenuNode.VIEW_MENU.item( "Spectrum", "canvas" ).item( "Overlapping" );
	}

	@MenuPosition
	public MenuNode getMenu() {
		return this.menu;
	}

	@MenuAction
	public void toggleView() {
/*
		MsppManager manager = MsppManager.getInstance();
		SpectrumObject spectrum = null;

		if( OverlapViewer.canvas == null ) {
			OverlapCanvas canvas = new OverlapCanvas( "m/z", "Int." );
			GuiManager gui = GuiManager.getInstance();
			MainFrame mainFrame = gui.getMainFrame();
			mainFrame.addSpectrumWindow( "Overlapping",  canvas );
			OverlapViewer.canvas = canvas;

			if( spectrum != null ) {
				this.addSpectrum( spectrum );
			}
		}
*/
	}

	@MenuCheck
	public boolean isVisible() {
		OverlapCanvas canvas = OverlapViewer.canvas;
		if( canvas == null ) {
			return false;
		}
		return canvas.isVisible();
	}

	@OpenSpectrum
	public void addSpectrum( SpectrumObject spectrum ) {
		OverlapCanvas canvas = OverlapViewer.canvas;

		if( canvas != null ) {
			XYData xyData = spectrum.getXYData();
//			OverlapViewer.canvas.addXYData( xyData );
		}
	}
}
