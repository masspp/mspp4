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
package ninja.mspp.view.main;

import java.net.URL;
import java.util.ResourceBundle;

import org.springframework.stereotype.Component;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.layout.BorderPane;
import ninja.mspp.MsppManager;
import ninja.mspp.ObjectManager;
import ninja.mspp.annotation.method.OnMainFrame;

/**
 * main frame
 * @author Satoshi Tanaka
 *
 */
@Component
public class MainFrame implements Initializable {
	@FXML
	private BorderPane rawDataPane;

	@FXML
	private BorderPane projectPane;

	/**
	 * constructor
	 */
	public MainFrame() {
	}

	/**
	 * sets the raw data pane
	 * @param node raw data pane
	 */
	public void setRawDataPane( Node node ) {
		this.rawDataPane.setCenter( node );
	}

	/**
	 * sets the project pane
	 * @param node project pane
	 */
	public void setProjectPane( Node node ) {
		this.projectPane.setCenter( node );
	}

	@Override
	public void initialize(URL location, ResourceBundle resources)  {
		MsppManager manager = MsppManager.getInstance();
		ObjectManager objectManager = ObjectManager.getInstane();
		objectManager.setMainFrame( this );
		manager.invokeAll( OnMainFrame.class );
	}
}
