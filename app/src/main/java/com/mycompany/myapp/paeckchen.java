package com.mycompany.myapp;
import java.io.*;

public class paeckchen implements Serializable
{
	String nick;
	String botschaft;
	static int nr=0;

	public paeckchen(String nick, String botschaft)
	{
		this.nick = nick;
		this.botschaft = botschaft;
		nr++;
	}

	public static void setNr(int nr)
	{
		paeckchen.nr = nr;
	}

	public static int getNr()
	{
		return nr;
	}

	public void setBotschaft(String botschaft)
	{
		this.botschaft = botschaft;
	}

	public String getBotschaft()
	{
		return botschaft;
	}

	


	public void setNick(String nick)
	{
		this.nick = nick;
	}

	public String getNick()
	{
		return nick;
	}}
