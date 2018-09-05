package com.shardbytes.music.client;

import com.shardbytes.music.common.Album;
import com.shardbytes.music.common.Song;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.WindowConstants;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

public class Client{
	
	private Socket socket;
	private ObjectOutputStream toServer;
	private ObjectInputStream fromServer;
	
	//Swing
	private JTextField nicknameField;
	
	public static void main(String[] args){
		
		new Client().startGUI();
		
	}
	
	/**
	 * Quick, dirty and temporary Swing UI for better control
	 */
	private void startGUI(){
		JFrame frame = new JFrame("ShardBytes Music client");
		frame.setSize(800, 600);
		frame.setLayout(new FlowLayout());
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		
		JButton connectButton = new JButton("Connect to server");
		connectButton.addActionListener(this::connectToServer);
		frame.getContentPane().add(connectButton);
		
		JButton disconnectButton = new JButton("Disconnect from server");
		disconnectButton.addActionListener(this::disconnectFromServer);
		frame.getContentPane().add(disconnectButton);
		
		JLabel nicklabel = new JLabel("Nickname:");
		frame.getContentPane().add(nicklabel);
		
		nicknameField = new JTextField("MacBook Air");
		nicknameField.setPreferredSize(new Dimension(200, nicknameField.getPreferredSize().height));
		frame.getContentPane().add(nicknameField);
		
		JButton send0 = new JButton("Send 0");
		send0.addActionListener((e) -> sendMessage(0));
		frame.getContentPane().add(send0);
		
		JButton send1 = new JButton("Send 1");
		send1.addActionListener((e) -> {
			sendMessage(1);
			System.out.println(getSongList());
		});
		frame.getContentPane().add(send1);
		
		JLabel albumArt = new JLabel(new ImageIcon("Client/src/com/shardbytes/music/client/resources/fff.png"));
		
		JButton send2 = new JButton("Send 2");
		send2.addActionListener((e) -> {
			sendMessage(2);
			//albumArt.setIcon(new ImageIcon(getScaledImage(new ImageIcon(getAlbumList().get(0).getAlbumArt()).getImage(), 500, 500)));
			getAlbumList().forEach(album -> System.out.println(album.getTitle()));
			
		});
		
		JButton send3 = new JButton("Send 3");
		send3.addActionListener((e) -> {
			sendMessage(3);
			sendMessage(nicknameField.getText());
			Album album = getAlbum();
			albumArt.setIcon(new ImageIcon(getScaledImage(new ImageIcon(album.getAlbumArt()).getImage(), 500, 500)));
			System.out.println("album.getTitle() = " + album.getTitle());
			System.out.println("album.getArtist() = " + album.getArtist());
			System.out.println("album.getGenre() = " + album.getGenre());
			System.out.println("album.getYear() = " + album.getYear());
			album.getSongs().forEach(song -> {
				System.out.println("song.getTitle() = " + song.getTitle());
			});
			System.out.println("songs = " + album.getSongs());
			
		});
		
		frame.getContentPane().add(send2);
		frame.getContentPane().add(send3);
		frame.getContentPane().add(albumArt);
		
		frame.setVisible(true);
	}
	
	private Image getScaledImage(Image srcImg, int w, int h){
		BufferedImage resizedImg = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2 = resizedImg.createGraphics();
		
		g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		g2.drawImage(srcImg, 0, 0, w, h, null);
		g2.dispose();
		
		return resizedImg;
	}
	
	private void connectToServer(ActionEvent event){
		try{
			socket = new Socket("localhost", 8192);
			toServer = new ObjectOutputStream(socket.getOutputStream());
			fromServer = new ObjectInputStream(socket.getInputStream());
			
			String message = getMessage();
			if(message.equals("getNickname")){
				sendMessage(nicknameField.getText());
			}
			
		}catch(IOException e){
			System.err.println(e.getMessage());
		}
		
	}
	
	private void disconnectFromServer(ActionEvent event){
		sendMessage(60);
	}
	
	private String getMessage(){
		try{
			return (String)fromServer.readObject();
		}catch(IOException | ClassNotFoundException e){
			System.err.println(e.getMessage());
		}
		return null;
	}
	
	private ArrayList<Song> getSongList(){
		try{
			return (ArrayList<Song>)fromServer.readObject();
		}catch(IOException | ClassNotFoundException e){
			System.err.println(e.getMessage());
		}
		return null;
	}
	
	private ArrayList<Album> getAlbumList(){
		try{
			return (ArrayList<Album>)fromServer.readObject();
		}catch(IOException | ClassNotFoundException e){
			e.printStackTrace();
			System.err.println(e.getMessage());
		}
		return null;
	}
	
	private Album getAlbum(){
		try{
			return (Album)fromServer.readObject();
		}catch(IOException | ClassNotFoundException e){
			e.printStackTrace();
			System.err.println(e.getMessage());
		}
		return null;
	}
	
	private void sendMessage(Object message){
		try{
			toServer.writeObject(message);
		}catch(IOException e){
			System.err.println(e.getMessage());
		}
		
	}
	
}
