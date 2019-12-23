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
 * @since Thu Feb 22 19:20:46 JST 2018
 *
 * Copyright (c) 2018 Satoshi Tanaka
 * All rights reserved.
 */
package ninja.mspp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import ninja.mspp.view.SpringFXMLLoader;
import ninja.mspp.view.main.MainFrame;

@SpringBootApplication
public class MsppApplication extends Application {
    // context
	private static ConfigurableApplicationContext context;

	/**
	 * main method
	 * @param args arguments
	 */
	public static void main(String[] args) {
		MsppApplication.context = SpringApplication.run( MsppApplication.class, args );
		launch( args );
	}

	@Override
	public void start( Stage stage ) throws Exception {
		MsppManager manager = MsppApplication.context.getBean( MsppManager.class );
		ObjectManager objectManager = ObjectManager.getInstane();

		manager.initialize();

		SpringFXMLLoader loader = manager.getFxmlLoader();
		objectManager.setFxmlLoader( loader );
		Parent parent = loader.load( MainFrame.class,  "MainFrame.fxml" );

		String title = manager.getConfig().getString( "mspp.title" );
		stage.setTitle( title );

		Image icon = new Image( getClass().getResourceAsStream( "/images/MS_icon_24.png" ) );
		stage.getIcons().add( icon );

		Scene scene = new Scene( parent );
		stage.setScene( scene );;

		stage.show();
	}

	@Override
	public void stop() throws Exception {
		MsppApplication.context.close();
	}
}
