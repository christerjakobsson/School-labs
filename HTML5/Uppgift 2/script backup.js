"use strict";
/*
 * Här bör du skapa en variabel som kan hålla 
 * reda på textnoden till paragrafen du håller 
 * på att skriva till
 */
var textnod = document.createTextnode(text);

/*
 * Du bör också ha en variabel som håller reda på
 * hur många paragrafer du har satt ut, så att du
 * kan märka upp dem med klasserna even/odd
 */
var paragraph;
 * Den här funktionen anropas då man trycker på en 

/* 
 * av knapparna med en bokstav, punkt, 
 * kommatecken eller mellanslag.
 *
 * Funktionsparametrar:
 * c - Variabel som innehåller ett tecken
 */
function addLetter(c){
	
	/*
	 * Här bör du kolla om din globala textnodsvariabel har ett värde,
	 * om den är null så anropar du addParagraph
	 */
	if(textnod === null)
	{
		addParagraph;
    }
	
	/*
	 * Här bör du lägga in tecknet c till den sparade textnoden
	 */
	textnod.appendData = c;
}
/*
 * Den här funktionen anroas då man trycker
 * på Enter knappen
 */
function addParagraph() {
	//Detta ska tas bort innan inlämning
	alert("Pressed letter: '+c+');
	textNode = "Hejsan";
	alert(textNode);
	 textnod.createTextNode(text);
	
	/*
	 * Sedan så lägger du till paragrafen till DOM trädet till 
	 * div-taggen som har id:t "output"
	 */
	 
	textnod.appendchild(output); 
	
	/*
	 * Du måste också märka upp paragrafen med klassen even/odd beroende
	 * på vad den föregående paragrafen märktes upp som
	 * (om detta är den första paragrafen så spelar det ingen roll
	 * vilken av even/odd som väljs)
	 */
	 textnod.currentTextNode = even;
    
	/*for(var i = 0; i < paragraph; i++)
    {
        if(textnod.lastchild == odd)
           {
			textnod.currentTextNode = textnod.setAttribute(class, even);
           } 
		else if (textnod.currentTextNode == even)
		{ 
			textnod.currentTextNode = odd;
		}
	
    } 
		*/
}

/*
 * Den här funktionen anropas när man trycker på länken "Räkna bokstäver".
 */
function countLetters() {
	var count=5;
	
	/*
	 * Här räknar du antalet bokstäver (a-z) som finns 
	 * i den nuvarande paragrafen (dvs i din globala textnod).
	 * Antalet bokstäver sparar du till variabeln 'count'.
	 */
	// count = textnod.length;
	/*
	 * Sen avslutar vi med att returnera count variabeln
	 */
	return count;
}
