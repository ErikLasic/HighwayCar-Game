package com.mygdx.game;

import WorldUnits.WorldUnitsGame;

import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;

public class DesktopLauncherWUG {
	public static void main (String[] arg) {
		Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
		config.setTitle("HighwayCar");
		new Lwjgl3Application(new WorldUnitsGame(), config);
	}
}
