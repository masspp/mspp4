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
package ninja.mspp.view;

import javafx.stage.Stage;
import ninja.mspp.view.main.MainFrame;

/**
 * gui manager
 */
public class GuiManager {
	// instance (This is the only object.)
	private static GuiManager instance;

	Stage stage;
	MainFrame mainFrame;

	/**
	 * constructor
	 */
	private GuiManager() {
		this.mainFrame = null;
	}

	/**
	 * sets the main frame
	 * @param frame main frame
	 */
	public void setMainFrame( MainFrame frame ) {
		this.mainFrame = frame;
	}

	/**
	 * gets the main frame
	 * @return main frame
	 */
	public MainFrame getMainFrame() {
		return this.mainFrame;
	}

	/**
	 * sets the stage
	 * @param stage stage
	 */
	public void setStage( Stage stage ) {
		this.stage = stage;
	}

	/**
	 * gets the stage
	 * @return stage
	 */
	public Stage getStage() {
		return this.stage;
	}

	/**
	 * gets the instance
	 * @return Gui manager instance. (This is the only object.)
	 */
	public static GuiManager getInstance() {
		if( GuiManager.instance == null ) {
			GuiManager.instance = new GuiManager();
		}
		return GuiManager.instance;
	}


}
