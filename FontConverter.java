package com.ingecep.font;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.imageio.ImageIO;

public class FontConverter 
{
	public void doConvert(Map<String, String> params)
	{
		String ttffile = params.get("font");
		String outputdir = params.get("output");
		
		outputdir = outputdir.endsWith("/") ? outputdir : outputdir + "/";
		
		int iconsize = 20;
		int fontsize = 18;

		File tfile;
		Font font;

		FileInputStream fi = null;
		
		BufferedImage image;
		Graphics2D graphics = null;
		
		Color fontcolor = Color.BLACK;
		
		if (params.containsKey("color"))
		{
			fontcolor = FontConverter.hex2Rgb(params.get("color"));
		}
		
		if (params.containsKey("iconsize"))
		{
			iconsize = Integer.parseInt(params.get("iconsize"));
		}
		
		if (params.containsKey("fontsize"))
		{
			iconsize = Integer.parseInt(params.get("fontsize"));
		}

		try
		{
			tfile = new File(ttffile);
			fi = new FileInputStream(tfile);
			font = Font.createFont(Font.TRUETYPE_FONT, fi);
			font = font.deriveFont(Font.PLAIN, fontsize);
			
			image = new BufferedImage(iconsize, iconsize, BufferedImage.TYPE_4BYTE_ABGR);
			graphics = image.createGraphics();
			
			graphics.setFont(font);
			
			AffineTransform af = new AffineTransform();
			FontRenderContext frc = new FontRenderContext(af, true, true); 
			
			Map<String, String> fontmap = loadFontMap(params.get("list"));
			Iterator<String> fontkey = fontmap.keySet().iterator();
			
			graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			
			while(fontkey.hasNext())
			{
				String fname = fontkey.next();
				String fvalue = fontmap.get(fname);
				
				System.out.println(">> exporting : " + fname);
				
				Rectangle2D rect = font.getStringBounds(fvalue, frc);
				
				double fw = rect.getWidth();
				double fh = rect.getHeight();
				
				graphics.setComposite(AlphaComposite.Clear);
				graphics.fillRect(0, 0, iconsize, iconsize);
				
				graphics.setComposite(AlphaComposite.Src);
				graphics.setColor(fontcolor);
				graphics.drawString(fvalue, (int) (iconsize - fw) / 2, (int) (fh - (iconsize - fh) / 2));
				ImageIO.write(image, "PNG", new File(outputdir + fname + ".png"));
			}
			
			System.out.println(">> finish exporting");
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
		finally {
			try {
				if (graphics != null) {
					graphics.dispose();
				}

				if (fi != null) {
					fi.close();
				}
			} catch (Exception ignored) {
			}
		}
	}
	
	public static Color hex2Rgb(String colorStr) 
	{
		return new Color(
	            Integer.valueOf( colorStr.substring( 1, 3 ), 16 ),
	            Integer.valueOf( colorStr.substring( 3, 5 ), 16 ),
	            Integer.valueOf( colorStr.substring( 5, 7 ), 16 ) );
	}
	
	public Map<String, String> loadFontMap(String listfilename)
	{
		Map<String, String> fontmap = new HashMap<String, String>();
		
		File f = null;
		FileReader fr = null;
	    BufferedReader br = null;
	    
	    String line = null;
	    
		try {
			f = new File(listfilename);

			fr = new FileReader(f);
			br = new BufferedReader(fr);

			while ((line = br.readLine()) != null) {
				if (!line.startsWith(";") && !line.startsWith("//")) // comment
				{
					int n = line.indexOf(":");
					if (n > -1) {
						String name = line.substring(0, n).trim();
						String value = line.substring(n + 1).trim();

						if (value.endsWith(",")) {
							value = value.substring(0, value.length() - 1).trim();
						}

						if (name.startsWith("\"") && name.endsWith("\"") && name.length() > 2) {
							name = name.substring(1, name.length() - 1);
						}

						if (value.startsWith("\"") && value.endsWith("\"") && value.length() > 2) {
							value = value.substring(1, value.length() - 1);

							if (value.startsWith("\\") && value.charAt(1) == 'u') {
								int code = Integer.parseInt(value.substring(2), 16);
								char ch = Character.toChars(code)[0];
								value = Character.toString(ch);
							}
						}

						fontmap.put(name, value);
					}
				}
			}
		}
		catch (Exception ex)
		{
			System.err.println(">> Error while reading list file : " + listfilename);
		}
		finally {
			try {
				if (br != null)
					br.close();

				if (fr != null)
					fr.close();
			} catch (Exception ignored) {
			}
		}
		
		return fontmap;
	}
	
	public static void printUsage()
	{
		System.err.println("usage: java com/ingecep/font/FontConverter [--help] [--color=COLOR] ");
		System.err.println("          [--font=FONT] [--list=LIST] [--iconsize=SIZE] [--fontsize=SIZE]");
		System.err.println(">> INGECEP FontConverter: error: too few arguments");
	}
	
	public static void main(String[] args) 
	{
		Map<String, String> params = new HashMap<>();

		for (int i=0; i < args.length; i++)
		{
			String arg = args[i];
			String value = null;

			if (arg.startsWith("--"))
			{
				arg = arg.substring(2);

				int n = arg.indexOf("=");
				if (n > -1)
				{
					value = arg.substring(n+1);
					arg = arg.substring(0, n);
				}

				if (arg.equals("help"))
				{
					printUsage();
					return;
				}
				else if (value != null && !value.equals(""))
				{
					params.put(arg, value);
				}
			}
		}
		
		if (!params.containsKey("font") || !params.containsKey("output") || !params.containsKey("list"))
		{
			printUsage();
			return;
		}
		
		FontConverter font = new FontConverter();
		font.doConvert(params);
	}
}
