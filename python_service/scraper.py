from bs4 import BeautifulSoup
import requests
import re
import json
from model import SolarKitClassifier
import time
import urllib.parse

class SolarKitScraper:
    def __init__(self):
        self.classifier = SolarKitClassifier()
        self.headers = {
            'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36'
        }
        
    def extract_price(self, text):
        price_pattern = r'R\$\s*([\d,.]+)'
        match = re.search(price_pattern, text)
        if match:
            price_str = match.group(1).replace('.', '').replace(',', '.')
            return float(price_str)
        return None
        
    def extract_power(self, text):
        power_pattern = r'(\d+(?:[,.]\d+)?)\s*[kK][wW]'
        match = re.search(power_pattern, text)
        if match:
            power_str = match.group(1).replace(',', '.')
            return float(power_str)
        return None
        
    def search_solar_kits(self, target_power, roof_type):
        kits = []
        search_queries = [
            f"kit energia solar {target_power}kw {roof_type} completo",
            f"sistema fotovoltaico {target_power}kw {roof_type}",
            f"gerador solar {target_power}kw {roof_type}"
        ]
        
        for query in search_queries:
            try:
                encoded_query = urllib.parse.quote(query)
                url = f"https://www.google.com/search?q={encoded_query}"
                
                response = requests.get(url, headers=self.headers)
                soup = BeautifulSoup(response.text, 'html.parser')
                
                # Encontrar links de produtos
                for result in soup.find_all('div', class_='g'):
                    try:
                        link = result.find('a')
                        if not link:
                            continue
                            
                        product_url = link['href']
                        if not product_url.startswith('http'):
                            continue
                            
                        # Verificar se é um site de e-commerce conhecido
                        if not any(site in product_url for site in [
                            'mercadolivre.com.br',
                            'americanas.com.br',
                            'magazineluiza.com.br',
                            'casasbahia.com.br'
                        ]):
                            continue
                            
                        # Buscar página do produto
                        product_response = requests.get(product_url, headers=self.headers)
                        product_soup = BeautifulSoup(product_response.text, 'html.parser')
                        
                        title = product_soup.find('h1')
                        title = title.text if title else ''
                        
                        description = product_soup.find('meta', {'name': 'description'})
                        description = description['content'] if description else ''
                        
                        # Procurar preço
                        price = None
                        price_elements = product_soup.find_all(text=re.compile(r'R\$\s*[\d,.]+'))
                        for elem in price_elements:
                            price = self.extract_price(elem)
                            if price:
                                break
                                
                        # Verificar potência
                        power = self.extract_power(title + ' ' + description)
                        
                        if price and power and 0.8 <= power/target_power <= 1.2:
                            kit = {
                                'title': title,
                                'description': description,
                                'price': price,
                                'power': power,
                                'url': product_url,
                                'site': product_url.split('/')[2],
                                'roof_type': roof_type
                            }
                            if kit not in kits:  # Evitar duplicatas
                                kits.append(kit)
                                
                    except Exception as e:
                        print(f"Erro ao processar produto: {e}")
                        continue
                        
            except Exception as e:
                print(f"Erro na busca: {e}")
                continue
                
        # Usar IA para ranquear os resultados
        ranked_kits = self.classifier.rank_kits(kits, target_power, roof_type)
        return ranked_kits[:10]  # Retornar os 10 melhores resultados
