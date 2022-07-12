package fr.metropolis.gestion;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.Optional;
import java.util.ServiceLoader;

public class PluginLoader {

	public void loadPlugin(){
		File[] files = new File("./plugins").listFiles();
		if(files == null){
			return;
		}
		for(File f : files){
			URLClassLoader pluginClassLoader = createClassLoader(f);
			ClassLoader currentClassLoader = Thread.currentThread().getContextClassLoader();
			try{
				Thread.currentThread().setContextClassLoader(pluginClassLoader);
				for(Plugin p : ServiceLoader.load(Plugin.class, pluginClassLoader)){
					installPlugin(p);
				}
			}finally {
				Thread.currentThread().setContextClassLoader(currentClassLoader);
			}
		}
	}

	private void installPlugin(Plugin plugin){
		System.out.println("Installing plugin ".concat(plugin.getClass().getName()));
		plugin.init();
	}

	public URLClassLoader createClassLoader(File dir){
		URL[] urls = Arrays.stream(Optional.of(dir.listFiles()).orElse(new File[]{}))
				.sorted()
				.map(File::toURI)
				.map(this::toUrl)
				.toArray(URL[]::new);
		return new PluginClassLoader(urls, getClass().getClassLoader());
	}

	private URL toUrl(URI uri){
		try{
			return uri.toURL();
		}catch (MalformedURLException e){
			throw new RuntimeException(e);
		}
	}
}

