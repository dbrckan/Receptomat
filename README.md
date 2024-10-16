# Receptomat

## Projektni tim
Nika Antolić (nantolic22)

David Brckan (dbrckan22)

Franjo Čotić (fcotic22)

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
F02 | Prijava | Za pristup aplikaciji potrebna je autentifikacija tako što korisnik unosi korisničko ime i lozinku ako već ima kreiran korisnički račun. | David Brckan
F03 | Prikaz recepta | Aplikacija mora omogućiti pregledavanje dostupnih recepata. | Nika Antolić
F04 | Kreiranje recepta | Aplikacija nudi mogućnost kreiranja novog recepta sa svim potrebnim sastojcima i količinom kao i spremanje istog. | David Brckan
F05 | Uređivanje recepta | Aplikacija nudi mogućnost uređivanja postojećih recepata koje je korisnik dodao. | Franjo Čotić
F06 | Brisanje recepta | Aplikacija nudi mogućnost brisanja recepata koje je korisnik doda. | Franjo Čotić
F07 | Dodavanje sastojaka u listu za kupovinu | Aplikacija omogućuje korisniku dodavanje sastojaka s određenog recepta u listu za kupovinu te omogućuje ručno dodavanje novih stavki koje nisu povezane s određenim receptom. | Nika Antolić
F08 | Brisanje sastojaka iz liste za kupovinu | Korisnik treba imati mogućnost ukloniti sastojke s liste za kupovinu kad ih više ne treba ili ih je već kupio. | David Brckan
F09 | Upravljanje favoritima | Korisnik treba imati mogućnost označiti recepte kao omiljene te ih pohraniti u zasebnu listu za brži pristup. | Nika Antolić
F10 | Pretraživanje recepata | Aplikacija treba omogućiti korisniku pretraživanje recepata prema ključnim riječima | David Brckan
F11 | Količina potrebnih sastojaka |Aplikacija sadrži mogućnost izračuna potrebne količine sastojaka prema zabilježenom receptu za određenu količinu hrane. | Franjo Čotić
F12 | Recenzije | Aplikacija omogućuje korisnicima ocijenjivanje i komentiranje recepta koje su isprobali čime se poboljšava korisničko iskustvo. | Franjo Čotić

## Tehnologije i oprema
Za implementaciju aplikacije koristit ćemo Android Studio s Kotlin programskim jezikom, dok će se za verzioniranje koristiti Git i GitHub. Projektna dokumentacija bit će vođena putem GitHub Wiki-ja, a planiranje i praćenje zadataka putem GitHub Projects. Kao literaturu koristit ćemo materijale s kolegija "Razvoj aplikacija za mobilne i pametne uređaje" te službenu Kotlin dokumentaciju dostupnu na https://kotlinlang.org/.

## Baza podataka i web server
Tražimo pristup serveru na kojemu ćemo moći imati bazu podataka.

## .gitignore
Uzmite u obzir da je u mapi Software .gitignore konfiguriran za nekoliko tehnologija, ali samo ako će projekti biti smješteni direktno u mapu Software ali ne i u neku pod mapu. Nakon odabira konačne tehnologije i projekta obavezno dopunite/premjestite gitignore kako bi vaš projekt zadovoljavao kriterije koji su opisani u ReadMe.md dokumentu dostupnom u mapi Software.
