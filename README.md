# GeoPic 🌍📸

GeoPic è un'applicazione desktop sviluppata in Java dedicata alla gestione avanzata di fotografie e gallerie multimediali, sia personali che condivise. 
Offre agli utenti un ambiente ricco per organizzare immagini, creare slideshow (video), taggare soggetti (anche collegati ad altri utenti) e gestire le informazioni relative a luoghi e metadati, il tutto tramite un'interfaccia grafica intuitiva.

## 🚀 Funzionalità Principali

- **Gestione Utenti:** Sistema di autenticazione integrato. Un pannello Amministratore dedicato permette la visualizzazione, l'aggiunta e la rimozione massiva degli utenti registrati.
- **Gallerie Personali e Condivise:** Organizza le tue fotografie in un'area privata (che supporta anche i salvataggi di slideshow) o sfoglia i contenuti delle gallerie condivise con altri utenti.
- **Gestione Multimediale:**
  - **Fotografie:** Visualizza le foto e i relativi metadati (dispositivo di scatto, data, luogo, visibilità, autori).
  - **Slideshow / Video:** Raggruppa le tue foto in progetti video ("Compone") e visualizzali tramite un carosello e un player integrato dedicati.
- **Tagging e Soggetti:** Identifica le persone, animali o categorie generiche all'interno delle tue fotografie. I soggetti possono corrispondere direttamente ad altri utenti registrati nel sistema.

## 🛠️ Architettura e Tecnologie

Il progetto è costruito su solide fondamenta architetturali per garantire manutenibilità ed estensibilità:
- **Linguaggio:** Java
- **Interfaccia Grafica:** Java Swing, arricchita da utility custom (come il `WrapLayout`) per assicurare un design responsivo e moderno.
- **Pattern MVC (Model-View-Controller):** 
  - Netta separazione tra la logica di dominio, l'aspetto visivo e la gestione degli eventi.
- **Pattern DAO (Data Access Object):** 
  - Astrazione completa del livello di persistenza tramite interfacce specifiche (`UtenteDAO`, `SoggettoDAO`, `VideoDAO`, `MostraDAO`, `ComponeDAO`) per le operazioni su database.

## 📂 Struttura del Progetto (Core Packages)

- `Model/`: Entità del dominio (es. `Fotografia`, `Galleria`, `Utente`, `Soggetto`, `Video`, `Luogo`).
- `GUI/`: Componenti visivi dell'applicazione, suddivisi logicamente in frame (es. `FinestraUtente`, `FinestraAdmin`), pannelli, contenitori e dialoghi.
- `Controller/`: Responsabile della mediazione tra le View della GUI e i dati forniti dal Model o dal Database.
- `DAO/`: Le interfacce che espongono i metodi CRUD per comunicare col database e popolare i model.

## 💻 Requisiti

- Java Development Kit (JDK) 21 o superiore (o una versione compatibile supportata).
- Un IDE raccomandato per i progetti Java (es. IntelliJ IDEA, Eclipse).
- Database relazionale configurato per gestire la persistenza dei dati (necessario per l'implementazione dei moduli DAO).

## ⚙️ Come avviare l'applicazione

1. Clona il repository o scarica il codice sorgente.
2. Importa il progetto all'interno del tuo IDE (ad es. IntelliJ IDEA).
3. Assicurati che le dipendenze e le connessioni al database siano propriamente configurate (tramite le eventuali implementazioni dei DAO).
4. Compila ed esegui la classe di avvio (tipicamente dove risiede il metodo `main` che istanzia il `Controller` e lancia il Login/`FinestraUtente`).

---
*Sviluppato come progetto per la gestione intelligente di foto e metadati.*
