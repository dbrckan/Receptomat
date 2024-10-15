# Inicijalne upute za prijavu projekta iz Razvoja aplikacija za mobilne i pametne uređaje

Poštovane kolegice i kolege, 

čestitamo vam jer ste uspješno prijavili svoj projektni tim na kolegiju Razvoj aplikacija za mobilne i pametne uređaje, te je za vas automatski kreiran repozitorij koji ćete koristiti za verzioniranje vašega koda i za jednostavno dokumentiranje istoga.

Ovaj dokument (README.md) predstavlja **osobnu iskaznicu vašeg projekta**. Vaš prvi zadatak je **prijaviti vlastiti projektni prijedlog** na način da ćete prijavu vašeg projekta, sukladno uputama danim u ovom tekstu, napisati upravo u ovaj dokument, umjesto ovoga teksta.

Za upute o sintaksi koju možete koristiti u ovom dokumentu i kod pisanje vaše projektne dokumentacije pogledajte [ovaj link](https://guides.github.com/features/mastering-markdown/).
Sav programski kod potrebno je verzionirati u glavnoj **master** grani i **obvezno** smjestiti u mapu Software. Sve artefakte (npr. slike) koje ćete koristiti u vašoj dokumentaciju obvezno verzionirati u posebnoj grani koja je već kreirana i koja se naziva **master-docs** i smjestiti u mapu Documentation.

Nakon vaše prijave bit će vam dodijeljen mentor s kojim ćete tijekom semestra raditi na ovom projektu. Mentor će vam slati povratne informacije kroz sekciju Discussions također dostupnu na GitHubu vašeg projekta. A sada, vrijeme je da prijavite vaš projekt. Za prijavu vašeg projektnog prijedloga molimo vas koristite **predložak** koji je naveden u nastavku, a započnite tako da kliknete na *olovku* u desnom gornjem kutu ovoga dokumenta :) 

# Naziv projekta
Receptomat
(u redak iznad navedite kratki proizvoljni naziv projekta prikladan akademskoj zajednici, a ovaj tekst kao i uvodni tekst iznad obrišite)

## Projektni tim
Nika Antolić (nantolic22)

David Brckan (dbrckan22)

Franjo Čotić (fcotic22)

(svi članovi tima moraju biti iz iste seminarske grupe)

Ime i prezime | E-mail adresa (FOI) | JMBAG | Github korisničko ime | Seminarska grupa
------------  | ------------------- | ----- | --------------------- | ----------------
David Brckan | dbrckan22@foi.hr | 0016159400 | dbrckan22 | G01
Nika Antolić | nantolic22@student.foi.hr | 0016160424 | nantolic22 | G01
Franjo Čotić | ... | ... | ... | ...

## Opis domene
Umjesto ovih uputa opišite domenu ili problem koji pokrivate vašim projektom. Domena može biti proizvoljna, ali obratite pozornost da sukladno ishodima učenja, domena omogući primjenu zahtijevanih koncepata kako je to navedeno u sljedećem poglavlju. Priložite odgovarajuće skice gdje je to prikladno.

## Specifikacija projekta
Umjesto ovih uputa opišite zahtjeve za funkcionalnošću mobilne aplikacije ili aplikacije za pametne uređaje. Pobrojite osnovne funkcionalnosti i za svaku naznačite ime odgovornog člana tima. Opišite osnovnu buduću arhitekturu programskog proizvoda. Obratite pozornost da mobilne aplikacije često zahtijevaju pozadinske servise. Također uzmite u obzir da bi svaki član tima trebao biti odgovoran za otprilike 3 funkcionalnosti, te da bi opterećenje članova tima trebalo biti ujednačeno. Priložite odgovarajuće dijagrame i skice gdje je to prikladno. Funkcionalnosti sustava bobrojite u tablici ispod koristeći predložak koji slijedi:

Oznaka | Naziv | Kratki opis | Odgovorni član tima
------ | ----- | ----------- | -------------------
F01 | Registracija | Za pristup aplikaciji potrebna je izrada korisničkog računa kako bi korisnik mogao pristupiti svim funkcionalnostima aplikacije. | David Brckan
F02 | Prijava | Za pristup aplikaciji potrebna je autentifikacija tako što korisnik unosi korisničko ime i lozinku ako već ima kreiran korisnički račun. | ...
F03 | Kreiranje recepta | Aplikacija nudi mogućnost kreiranja novog recepta sa svim potrebnim sastojcima i količinom kao i spremanje istog. | ...
F04 | Izračun količine sastojaka | Aplikacija sadrži mogućnost izračuna potrebne količine sastojaka prema zabilježenom receptu za određenu količinu hrane. | ...
F05 | Kalkulator | Aplikacija sadrži integrirani kalkulator koji omogućava ručni izračun prilikom korištenja aplikacije ako je to potrebno. | ...
F06 | Konverzija mjernih jedinica | Aplikacija sadrži integriranu opciju preračunavanja iz jedne mjerne jedinice u neku drugu. | ...
F07 | Kreiranje liste sastojaka za kupnju | Aplikacija nudi mogućnost kreiranja liste sastojaka i količine istih za kupnju ovisno o odabranom receptu. | ...
F08 | Forum | Aplikacija nudi mogućnost komunikacije između različitih korisnika pomoću integriranog foruma koji može služiti i za dijeljenje recepata. | ...
F09 | Pretraživanje recepata | Aplikacija nudi mogućnost pretraživanja recepata prema ključnim riječima. | ...

## Tehnologije i oprema
Umjesto ovih uputa jasno popišite sve tehnologije, alate i opremu koju ćete koristiti pri implementaciji vašeg rješenja. Vaše rješenje može biti implementirano u bilo kojoj tehnologiji za razvoj mobilnih aplikacija ili aplikacija za pametne uređaje osim u hibridnim web tehnologijama kao što su React Native ili HTML+CSS+JS. Tehnologije koje ćete koristiti bi trebale biti javno dostupne, a ako ih ne budemo obrađivali na vježbama u vašoj dokumentaciji ćete morati navesti način preuzimanja, instaliranja i korištenja onih tehnologija koje su neopbodne kako bi se vaš programski proizvod preveo i pokrenuo. Pazite da svi alati koje ćete koristiti moraju imati odgovarajuću licencu. Što se tiče zahtjeva nastavnika, obvezno je koristiti git i GitHub za verzioniranje programskog koda, GitHub Wiki za pisanje jednostavne dokumentacije sukladno uputama mentora, a projektne zadatke je potrebno planirati i pratiti u alatu GitHub projects.

## Baza podataka i web server
Tražimo pristup serveru na kojemu ćemo moći imati bazu podataka.

## .gitignore
Uzmite u obzir da je u mapi Software .gitignore konfiguriran za nekoliko tehnologija, ali samo ako će projekti biti smješteni direktno u mapu Software ali ne i u neku pod mapu. Nakon odabira konačne tehnologije i projekta obavezno dopunite/premjestite gitignore kako bi vaš projekt zadovoljavao kriterije koji su opisani u ReadMe.md dokumentu dostupnom u mapi Software.
