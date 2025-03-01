from flask import Flask, request, jsonify
from flask_cors import CORS
from scraper import SolarKitScraper
import traceback
import threading
import queue

app = Flask(__name__)
CORS(app)
scraper = SolarKitScraper()
search_cache = {}
search_queue = queue.Queue()

def background_search():
    while True:
        try:
            search_id, power, roof_type = search_queue.get()
            results = scraper.search_solar_kits(power, roof_type)
            search_cache[search_id] = {
                'status': 'complete',
                'results': results
            }
        except Exception as e:
            search_cache[search_id] = {
                'status': 'error',
                'error': str(e)
            }
        finally:
            search_queue.task_done()

# Iniciar thread de busca em background
search_thread = threading.Thread(target=background_search, daemon=True)
search_thread.start()

@app.route('/search', methods=['POST'])
def search_kits():
    try:
        data = request.get_json()
        power = float(data.get('power', 0))
        roof_type = data.get('roof_type', '')
        
        if not power or not roof_type:
            return jsonify({'error': 'Power and roof_type are required'}), 400
        
        search_id = f"{power}_{roof_type}"
        
        # Verificar cache
        if search_id in search_cache:
            result = search_cache[search_id]
            if result['status'] == 'complete':
                return jsonify(result['results'])
            elif result['status'] == 'error':
                return jsonify({'error': result['error']}), 500
        
        # Iniciar nova busca
        search_cache[search_id] = {'status': 'searching'}
        search_queue.put((search_id, power, roof_type))
        
        return jsonify({
            'status': 'searching',
            'message': 'Search started. Please check back in a few seconds.'
        })
        
    except Exception as e:
        print(f"Erro: {str(e)}")
        traceback.print_exc()
        return jsonify({'error': str(e)}), 500

@app.route('/status/<search_id>', methods=['GET'])
def check_status(search_id):
    try:
        if search_id in search_cache:
            return jsonify(search_cache[search_id])
        return jsonify({'status': 'not_found'}), 404
        
    except Exception as e:
        print(f"Erro: {str(e)}")
        traceback.print_exc()
        return jsonify({'error': str(e)}), 500

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5000)
