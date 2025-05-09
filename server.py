import cryptography
import requests

from flask import Flask, jsonify, request, send_file
import pymysql

# Configurazione Flask
app = Flask(__name__)

# Funzione per ottenere una connessione al database
def get_db_connection():
    return pymysql.connect(
        host="localhost",
        port=3306,
        user='root',
        password='gemma2002',
        database='albergo',
        cursorclass=pymysql.cursors.DictCursor  # Restituisce i risultati come dizionari
    )

@app.route('/login', methods=['POST'])
def login():
    try:
        # Ricevi i dati JSON dal client
        data = request.json
        email = data.get('email')
        password = data.get('password')

        # Controllo dei parametri
        if not email or not password:
            return jsonify({"error": "Email e password sono obbligatori"}), 400

        # Connessione al database
        connection = get_db_connection()

        with connection.cursor() as cursor:
            # Modifica la query per includere l'ID utente
            query = "SELECT id, nome, cognome, email FROM Utenti WHERE email = %s AND password = %s"
            cursor.execute(query, (email, password))
            user = cursor.fetchone()
            print(f"Utente trovato: {user}")  # Aggiungi questo log per vedere i dati trovati

        connection.close()  # Chiudi la connessione

        # Risposta in base al risultato della query
        if user:
            return jsonify({
                "success": True,
                "message": "Login riuscito",
                "user": {
                    "id": user['id'],           # Assicurati di includere l'ID utente
                    "name": user['nome'],
                    "surname": user['cognome'],
                    "email": user['email']
                }
            }), 200
        else:
            return jsonify({"success": False, "message": "Credenziali non valide"}), 401

    except Exception as e:
        print(f"Errore: {e}")
        return jsonify({"error": str(e)}), 500


#endpoint per la home su html
@app.route('/')
def home():
    return "Ciao, il server Flask è attivo!"

@app.route('/users', methods=['GET'])
def get_users():
    try:
        # Connessione al database
        connection = get_db_connection()

        with connection.cursor() as cursor:
            # Esegui una query per ottenere tutti gli utenti
            cursor.execute("SELECT * FROM Utenti")
            users = cursor.fetchall()  # Ottieni tutti i risultati

        connection.close()

        # Restituisci i risultati come JSON
        return jsonify({"success": True, "users": users}), 200
    except Exception as e:
        # Gestione errori
        print(f"Errore durante il recupero degli utenti: {e}")
        return jsonify({"success": False, "error": str(e)}), 500


# Endpoint per aggiungere un nuovo utente
@app.route('/register', methods=['POST'])
def register():
    data = request.json
    nome = data.get('nome')
    cognome = data.get('cognome')
    email = data.get('email')
    password = data.get('password')

    if not nome or not cognome or not email or not password:
        return jsonify({"error": "Tutti i campi sono obbligatori"}), 400

    try:
        connection = get_db_connection()
        with connection.cursor() as cursor:
            # Esegui la query per inserire i dati
            query = """
                INSERT INTO Utenti (nome, cognome, email, password)
                VALUES (%s, %s, %s, %s)
            """
            cursor.execute(query, (nome, cognome, email, password))
            connection.commit()
        connection.close()

        return jsonify({"success": True, "message": "Utente registrato con successo"}), 201
    except Exception as e:
        return jsonify({"error": str(e)}), 500

@app.route('/info_albergo', methods=['GET'])
def info_albergo():
    try:
        descrizione = requests.get("https://raw.githubusercontent.com/maybegem/Albergo-prg/main/info.json").json().get("descrizione", "Descrizione non disponibile")
    except:
        descrizione = "Errore nel caricamento della descrizione"

    return jsonify({
        "nome": "Hotel Dimora Aurelia",
        "descrizione": descrizione,
        "immagine_url": "https://raw.githubusercontent.com/maybegem/Albergo-prg/main/1.jpg"
    })

@app.route('/utente/modificaPassword', methods=['POST'])
def modifica_password():
    user_id = request.args.get('id')
    old_password = request.args.get('old_password')
    new_password = request.args.get('new_password')

    if not user_id or not new_password:
        return jsonify({"success": False, "message": "Dati mancanti"}), 400

    if not old_password:
        return jsonify({"error": "La vecchia password non è corretta"}), 401

    try:
        conn = get_db_connection()
        with conn.cursor() as cursor:
            # Controlla la vecchia password
            cursor.execute("SELECT password FROM Utenti WHERE id = %s", (user_id,))
            user = cursor.fetchone()

            if not user or user['password'] != old_password:
                return jsonify({"success": False, "message": "Vecchia password errata"}), 401

            # Aggiorna la password
            cursor.execute("UPDATE Utenti SET password = %s WHERE id = %s", (new_password, user_id))
            conn.commit()

        return jsonify({"success": True, "message": "Password modificata con successo"}), 200

    except Exception as e:
        print(f"Errore: {e}")
        return jsonify({"success": False, "message": "Errore del server"}), 500

    finally:
        if conn:
            conn.close()


@app.route('/prenotazioni', methods=['POST'])
def crea_prenotazione():
    try:
        data = request.json
        print("Ricevuta richiesta POST con dati:", data)  # Log per debug

        # Controllo che tutti i dati siano presenti
        if not all(k in data for k in ('user_id', 'check_in', 'check_out', 'adulti', 'bambini', 'colazione')):
            return jsonify({"error": "Dati mancanti"}), 400

        # Connessione al database
        conn = get_db_connection()
        with conn.cursor() as cursor:
            query = """
                INSERT INTO Prenotazioni (user_id, check_in, check_out, adulti, bambini, colazione)
                VALUES (%s, %s, %s, %s, %s, %s)
            """
            cursor.execute(query, (
                data['user_id'],
                data['check_in'],
                data['check_out'],
                data['adulti'],
                data['bambini'],
                data['colazione']
            ))
            conn.commit()

        conn.close()
        print("Prenotazione salvata con successo!")
        return jsonify({"success": True, "message": "Prenotazione creata con successo!"}), 201

    except Exception as e:
        print(f"Errore durante il salvataggio: {e}")  # Log dell'errore
        return jsonify({"error": str(e)}), 500


@app.route('/prenotazioni', methods=['GET']) #prende le prenotazioni
def get_prenotazioni():
    user_id = request.args.get('user_id')

    connection = get_db_connection()
    with connection.cursor() as cursor:
        cursor.execute("SELECT * FROM Prenotazioni WHERE user_id = %s", (user_id,))
        prenotazioni = cursor.fetchall()

    connection.close()
    return jsonify(prenotazioni)

@app.route('/servizi', methods=['GET'])
def get_servizi():
    # Qui aggiungiamo i servizi con immagini prese da GitHub
    servizi = [
        {
            "nome": "Ristorante",
            "descrizione": "https://raw.githubusercontent.com/maybegem/Albergo-prg/refs/heads/main/ristorante.json",
                #"Il ristorante La Tavola Aurea, situato all'interno dell'Hotel Dimora Aurelia, è un raffinato punto di incontro tra tradizione culinaria e atmosfera esclusiva. Caratterizzato da un design elegante con arredi in legno pregiato, il locale offre una vista suggestiva sul giardino interno o sulle dolci colline circostanti. La cucina propone piatti ispirati alle ricette regionali, utilizzando ingredienti freschi e di alta qualità. La Tavola Aurea è il luogo ideale per cene romantiche, eventi speciali o pranzi all’insegna del gusto e della raffinatezza.",
            "immagine_url": "https://raw.githubusercontent.com/maybegem/Albergo-prg/e9e2f7ab5647de100dcb05b87bc56336f833b14f/2.jpg"
        },
        {
            "nome": "Spa",
            "descrizione": "https://raw.githubusercontent.com/maybegem/Albergo-prg/refs/heads/main/spa.json",
                #"La Sala Massaggi del Centro Benessere Aurelia, situata all’interno dell’Hotel Dimora Aurelia, è un’oasi di pace e relax. Arredata con eleganza, la sala dispone di comodi lettini massaggio, illuminati da una luce soffusa e naturale proveniente da ampie finestre affacciate su un rigoglioso giardino. Elementi in legno, piante decorative e candele profumate contribuiscono a creare un’atmosfera serena e armoniosa. Qui, ogni dettaglio è studiato per offrire un’esperienza di benessere completa, ideale per rilassarsi e rigenerarsi con trattamenti esclusivi.",
            "immagine_url": "https://raw.githubusercontent.com/maybegem/Albergo-prg/e9e2f7ab5647de100dcb05b87bc56336f833b14f/3-spa.jpg"
        },
        {
            "nome": "Escursione",
            "descrizione": "https://raw.githubusercontent.com/maybegem/Albergo-prg/refs/heads/main/escursioni.json",
                #"Le escursioni organizzate dall'Hotel Dimora Aurelia offrono agli ospiti l'opportunità di immergersi nella bellezza naturale dei dintorni. Guidati da esperti del territorio, i partecipanti possono esplorare suggestivi sentieri tra colline ondulate, prati fioriti e boschi rigogliosi. Adatte a tutte le età, queste attività sono un’occasione perfetta per rigenerarsi all'aria aperta e vivere un’esperienza autentica a contatto con la natura.",
            "immagine_url": "https://raw.githubusercontent.com/maybegem/Albergo-prg/e9e2f7ab5647de100dcb05b87bc56336f833b14f/4-escursioni.jpg"
        }
    ]

    # Per ogni servizio, recupera la descrizione dal file JSON remoto
    for servizio in servizi:
        try:
            response = requests.get(servizio["descrizione"])
            if response.status_code == 200:
                data = response.json()
                servizio["descrizione"] = data.get("descrizione", "Descrizione non disponibile")
            else:
                servizio["descrizione"] = "Errore nel caricamento della descrizione"
        except Exception as e:
            servizio["descrizione"] = f"Errore: {str(e)}"

    return jsonify({"success": True, "servizi": servizi})


@app.route('/prenotazioni/servizi', methods=['GET'])
def get_prenotazioni_servizi():
    user_id = request.args.get('user_id')

    if not user_id:
        return jsonify({"success": False, "message": "ID utente mancante"}), 400

    try:
        conn = get_db_connection()
        with conn.cursor() as cursor:
            query = """
                SELECT id, tipo_servizio, data_servizio, orario, numero_persone
                FROM PrenotazioniServizi
                WHERE user_id = %s
            """
            cursor.execute(query, (user_id,))
            result = cursor.fetchall()

        prenotazioni_servizi = [
            {
                "id": row['id'],
                "tipo_servizio": row['tipo_servizio'],  # <-- Cambiato da nome_servizio
                "data_servizio": row['data_servizio'].isoformat(),
                "orario": str(row['orario']),
                "numero_persone": row['numero_persone']
            }
            for row in result
        ]

        return jsonify(prenotazioni_servizi), 200

    except Exception as e:
        print(f"Errore: {e}")
        return jsonify({"success": False, "message": "Errore durante il recupero delle prenotazioni"}), 500


@app.route('/utente/<int:user_id>', methods=['GET'])
def get_utente(user_id):
    try:
        conn = get_db_connection()
        with conn.cursor() as cursor:
            query = "SELECT nome, cognome, email FROM Utenti WHERE id = %s"
            cursor.execute(query, (user_id,))
            user = cursor.fetchone()

            if not user:
                return jsonify({"success": False, "message": "Utente non trovato"}), 404

            # Restituisci i dettagli dell'utente
            return jsonify({
                "success": True,
                "nome": user['nome'],
                "cognome": user['cognome'],
                "email": user['email']
            }), 200

    except Exception as e:
        print(f"Errore: {e}")
        return jsonify({"success": False, "message": "Errore del server"}), 500

    finally:
        if conn:
            conn.close()


@app.route('/prenotazioni/stanze', methods=['GET'])
def get_prenotazioni_stanze():
    user_id = request.args.get('user_id')

    # Log per vedere il risultato della query
    print(f"Richiesta ricevuta per user_id: {user_id}")

    if not user_id:
        return jsonify({"success": False, "message": "ID utente mancante"}), 400

    try:
        conn = get_db_connection()
        with conn.cursor() as cursor:
            query = """
                SELECT id, check_in, check_out, adulti, bambini, colazione
                FROM Prenotazioni
                WHERE user_id = %s
            """
            cursor.execute(query, (user_id,))
            result = cursor.fetchall()

            print("Dati recuperati dal database:", result)  # Log dei dati per verifica

        # Costruzione dell'elenco di prenotazioni
        prenotazioni = [
            {
                "id": row['id'],
                "check_in": row['check_in'].isoformat() if row['check_in'] else None,
                "check_out": row['check_out'].isoformat() if row['check_out'] else None,
                "adulti": row['adulti'],
                "bambini": row['bambini'],
                "colazione": row['colazione'],
            }
            for row in result
        ]

        # Risposta come array puro
        return jsonify(prenotazioni), 200

    except Exception as e:
        print(f"Errore: {e}")
        return jsonify({"success": False, "message": "Errore durante il recupero delle prenotazioni"}), 500

    finally:
        if conn:
            conn.close()


# Endpoint per creare una prenotazione di un servizio
@app.route('/prenotazione_servizio', methods=['POST'])
def crea_prenotazione_servizio():
    try:
        data = request.json
        print("Ricevuta richiesta POST con dati:", data)  # Log per debug

        # Controllo che tutti i dati siano presenti
        if not all(k in data for k in ('user_id', 'tipo_servizio', 'data_servizio', 'orario', 'numero_persone')):
            return jsonify({"error": "Dati mancanti"}), 400

        # Connessione al database
        connection = get_db_connection()
        with connection.cursor() as cursor:
            query = """
                INSERT INTO PrenotazioniServizi (data_servizio, numero_persone, orario, tipo_servizio, user_id)
                VALUES (%s, %s, %s, %s, %s)
            """
            cursor.execute(query, (
                data['data_servizio'],
                data['numero_persone'],
                data['orario'],
                data['tipo_servizio'],
                data['user_id']
            ))
            connection.commit()

        connection.close()
        print("Prenotazione salvata con successo!")
        return jsonify({"success": True, "message": "Prenotazione creata con successo!"}), 201

    except Exception as e:
        print(f"Errore durante il salvataggio: {e}")  # Log dell'errore
        return jsonify({"error": str(e)}), 500

if __name__ == '__main__':
    app.run(debug=True)



