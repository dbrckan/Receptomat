# Receptomat

## Projektni tim
Ime i prezime | E-mail adresa (FOI) | JMBAG | Github korisničko ime | Seminarska grupa
------------  | ------------------- | ----- | --------------------- | ----------------
David Brckan | dbrckan22@student.foi.hr | 0016159400 | dbrckan22 | G01
Nika Antolić | nantolic22@student.foi.hr | 0016160424 | nantolic22 | G01
Franjo Čotić | fcotic22@student.foi.hr | 0016160029 | fcotic22 | G01

## Opis domene
Ova mobilna aplikacija korisnicima omogućava jednostavan i učinkovit način organizacije, pretrage i prilagodbe recepata s dodatkom opcija za izračun količine sastojaka, upravljanje listom za kupovinu i pohranu recepata. Aplikacija je usmjerena na korisnike koji traže praktične alate za prilagođavanje recepata svojim potrebama i poboljšanje procesa pripreme jela. Registracija i prijava omogućava korisnicima stvaranje vlastitih recepata i ocjenjivanja te komentiranje drugih recepata.

## Specifikacija projekta

Oznaka | Naziv | Kratki opis | Odgovorni član tima
------ | ----- | ----------- | -------------------
F01 | Registracija | Za pristup aplikaciji potrebna je izrada korisničkog računa kako bi korisnik mogao pristupiti svim funkcionalnostima aplikacije. | Nika Antolić
F02 | Prijava | Za pristup aplikaciji potrebna je autentifikacija tako što korisnik unosi korisničko ime i lozinku ako već ima kreiran korisnički račun. Omogućena je i dvofaktorska (2FA) autentifikacija koja uključuje dodatan kod poslan putem e-pošte. | David Brckan
F03 | Upravljanje receptima |Aplikacija omogućuje korisnicima pregledavanje, kreiranje, uređivanje i brisanje recepata. Korisnici mogu dodavati nove recepte s potrebnim sastojcima, ažurirati postojeće recepte te brisati recepte koje više ne žele imati u aplikaciji. | Nika Antolić
F04 | Upravljanje listom za kupovinu | Aplikacija omogućuje korisnicima prikaz same liste te dodavanje sastojaka na listu za kupovinu s bilo kojeg recepta ili ručno unesenih stavki. Također, omogućuje korisnicima uklanjanje sastojaka s liste kada ih više ne trebaju ili su ih već kupili. | David Brckan
F05 | Korisnički profil | Korisnici mogu prilagoditi profil svojim preferencijama (npr. vegetarijanski, bezglutenski), mogu ažurirati svoje podatke te prilagoditi postavke privatnosti. Također, korisnici mogu odabrati željenu razinu obavijesti. | David Brckan
F06 | Upravljanje favoritima | Korisnik treba imati mogućnost označiti recepte kao omiljene te ih pohraniti u zasebnu listu za brži pristup. | Franjo Čotić
F07 | Pretraživanje recepata | Aplikacija treba omogućiti korisniku pretraživanje recepata prema ključnim riječima | Nika Antolić
F08 | Planiranje obroka | Aplikacija omogućava korisnicima planiranje recepata za cijeli tjedan. Korisnici mogu odabrati recepte te ih rasporediti prema danima u tjednu. Također, na temelju planiranih recepata, aplikacija generira popis za kupovinu s potrebnim sastojcima i količinama. | Franjo Čotić
F09 | Recenzije | Aplikacija omogućuje korisnicima ocijenjivanje i komentiranje recepta koje su isprobali čime se poboljšava korisničko iskustvo. | Franjo Čotić

## Tehnologije i oprema
Za implementaciju aplikacije koristit ćemo Android Studio s Kotlin programskim jezikom, dok će se za verzioniranje koristiti Git i GitHub. Projektna dokumentacija bit će vođena putem GitHub Wiki-ja, a planiranje i praćenje zadataka putem GitHub Projects. Kao literaturu koristit ćemo materijale s kolegija "Razvoj aplikacija za mobilne i pametne uređaje" te službenu Kotlin dokumentaciju dostupnu na https://kotlinlang.org/.

## Baza podataka i web server
Tražimo pristup serveru na kojemu ćemo moći imati bazu podataka.

## .gitignore
Uzmite u obzir da je u mapi Software .gitignore konfiguriran za nekoliko tehnologija, ali samo ako će projekti biti smješteni direktno u mapu Software ali ne i u neku pod mapu. Nakon odabira konačne tehnologije i projekta obavezno dopunite/premjestite gitignore kako bi vaš projekt zadovoljavao kriterije koji su opisani u ReadMe.md dokumentu dostupnom u mapi Software.
