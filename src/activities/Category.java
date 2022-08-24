package activities;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.StringTokenizer;


import Main.Principale;


public class Category {
	
	
	private ArrayList<String> categories;
	private File f;
	
	public Category(String urlSource) {
		Path path= Paths.get(urlSource);
		String pathStr=""+path.toAbsolutePath();
		f= new File(pathStr);
		getAllCategories();
	}
	
	//legge le catgorie dal file di testo
	private void getAllCategories() {
		categories= new ArrayList<String>();
		Scanner read;
		try {
			read = new Scanner(f);
			while(read.hasNextLine()) {
				StringTokenizer st= new StringTokenizer(read.nextLine(), ", ");
				while(st.hasMoreElements()) {
					categories.add(""+st.nextElement());
				}
			}
			read.close();
			checkForJson();
		} catch (FileNotFoundException e) {
			System.out.print(e + "in get all categories");
		}

	}
	
	//controlla se esistano i file json delle categorie
	private void checkForJson() {
		try {
			for(String categoria: categories) {
				Path pathC= Paths.get(categoria+".json");
				String pathCStr=""+pathC.toAbsolutePath();
				f= new File(pathCStr);
				if(!f.exists())
					throw new FileNotFoundException(categoria);
			}
		}catch(FileNotFoundException e) {
			System.out.println(e+" in checking for categories jsons");
		}
	}
	
	//inserisce nella hashmap le scene degli workspace di presentazione delle attività
	public void generateWorkspaces() {
		for(String categoria: categories) {
			Principale.map.putIfAbsent(categoria, new Workspace(categoria).getWorkspaceScene());
		}
	}
	//inserisce il singolo workspace nella hashmap se esiste la categoria
	public void generateSingleWorkspce(String categoryName) {
		if(categories.contains(categoryName)) {
			Principale.map.put(categoryName, new Workspace(categoryName).getWorkspaceScene());
		}
		else {
			System.out.println("impossibile generare workspace perchè la categoria "+categoryName+" non esiste nell'elenco");
		}
	}
	
	public ArrayList<String> getCategories(){
		return categories;
	}
	//maybe useless
	public void addCategory(String newCategory) {
		categories.add(newCategory);
	}
	
}
