"use strict";
/*
 * Här bör du skapa en variabel som kan hålla 
 * reda på textnoden till paragrafen du håller 
 * på att skriva till
 */
var globalNode = "";
/*
 * Du bör också ha en variabel som håller reda på
 * hur många paragrafer du har satt ut, så att du
 * kan märka upp dem med klasserna even/odd
 */
var antPar = 0;

/* 
 * Den här funktionen anropas då man trycker på en 
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
	if ( globalNode === null) {
		addParagraph();
	}	

	/*
	 * Här bör du lägga in tecknet c till den sparade textnoden
	 */
	 
	globalNode += c;

}	

/*
 * Den här funktionen anroas då man trycker
 * på Enter knappenoutput
 */
function addParagraph() {
	/*
	 * Här skapar du en ny paragraf med tillhörande textnod. 
	 * Textnoden sparar du undan till din globala textnodsvariabel.
	 */
	 
	var p = document.createElement("p");
	
	var text = document.createTextNode(globalNode);
	
	p.appendChild(text);
	globalNode = "";

	/*
	 * Sedan så lägger du till paragrafen till DOM trädet till 
	 * div-taggen som har id:t "output"
	 */

	 document.getElementById("output").appendChild(p); 
	
	/*
	 * Du måste också märka upp paragrafen med klassen even/odd beroende
	 * på vad den föregående paragrafen märktes upp som
	 * (om detta är den första paragrafen så spelar det ingen roll
	 * vilken av even/odd som väljs)
	 */
	if(antPar % 2 === 0) {
		p.setAttribute("class", "even");
	}
	else {
		p.setAttribute("class", "odd");
	}
	antPar++;
}

/*
 * Den här funktionen anropas när man trycker på länken "Räkna bokstäver".
 */
function countLetters() {
	var count = 0;

	/*
	 * Här räknar du antalet bokstäver (a-z) som finns 
	 * i den nuvarande paragrafen (dvs i din globala textnod).
	 * Antalet bokstäver sparar du till variabeln 'count'.
	 */
	var parLength = globalNode.match(/[a-z]/g);
	if(parLength !== null) {
		count = parLength.length;
	}
	
	/*
	 * Sen avslutar vi med att returnera count variabeln
	 */
	return count;
}