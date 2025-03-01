class SolarKitClassifier:
    def __init__(self):
        self.price_weight = 0.4
        self.power_match_weight = 0.4
        self.description_weight = 0.2
        
        # Palavras-chave importantes para avaliar a qualidade do kit
        self.quality_keywords = {
            'inversor': 5,
            'painel': 5,
            'estrutura': 3,
            'completo': 4,
            'homologado': 4,
            'garantia': 4,
            'certificado': 4,
            'inmetro': 5,
            'instalação': 3,
            'grid': 3,
            'tie': 3,
            'on-grid': 4,
            'ongrid': 4,
            'monofásico': 2,
            'bifásico': 2,
            'trifásico': 2,
            'Canadian': 3,
            'Fronius': 3,
            'SMA': 3,
            'Growatt': 3,
            'Jinko': 3,
            'Longi': 3,
            'JA Solar': 3,
            'Risen': 3,
            'Trina': 3
        }
        
    def calculate_price_score(self, price, power_kw):
        """Calcula pontuação baseada no preço por kW"""
        if not price or not power_kw:
            return 0
            
        price_per_kw = price / power_kw
        
        # Faixa de preços típica para sistemas solares (R$/kW)
        min_price_per_kw = 3000  # Muito barato, pode ser suspeito
        ideal_price_per_kw = 4500  # Preço ideal
        max_price_per_kw = 7000  # Muito caro
        
        if price_per_kw < min_price_per_kw:
            return 0.3  # Pontuação baixa para preços muito baixos (podem ser incorretos)
        elif price_per_kw <= ideal_price_per_kw:
            return 1.0 - (price_per_kw - min_price_per_kw) / (ideal_price_per_kw - min_price_per_kw) * 0.3
        else:
            return max(0, 0.7 - (price_per_kw - ideal_price_per_kw) / (max_price_per_kw - ideal_price_per_kw) * 0.7)
            
    def calculate_power_match_score(self, kit_power, target_power):
        """Calcula pontuação baseada na correspondência de potência"""
        if not kit_power or not target_power:
            return 0
            
        power_ratio = kit_power / target_power
        
        if 0.9 <= power_ratio <= 1.1:
            return 1.0
        elif 0.8 <= power_ratio <= 1.2:
            return 0.8
        elif 0.7 <= power_ratio <= 1.3:
            return 0.5
        else:
            return 0.2
            
    def calculate_description_score(self, title, description):
        """Calcula pontuação baseada na qualidade da descrição"""
        if not title or not description:
            return 0
            
        text = (title + ' ' + description).lower()
        score = 0
        total_weight = 0
        
        for keyword, weight in self.quality_keywords.items():
            if keyword.lower() in text:
                score += weight
                total_weight += weight
                
        if total_weight == 0:
            return 0.3  # Pontuação mínima se nenhuma palavra-chave for encontrada
            
        normalized_score = score / (total_weight * 1.5)  # Fator 1.5 para não exigir todas as palavras
        return min(1.0, normalized_score)
        
    def rank_kits(self, kits, target_power, roof_type):
        """Classifica os kits solares com base em múltiplos critérios"""
        if not kits:
            return []
            
        scored_kits = []
        for kit in kits:
            try:
                # Calcular pontuações individuais
                price_score = self.calculate_price_score(kit.get('price'), kit.get('power'))
                power_score = self.calculate_power_match_score(kit.get('power'), target_power)
                desc_score = self.calculate_description_score(kit.get('title', ''), kit.get('description', ''))
                
                # Calcular pontuação final ponderada
                final_score = (
                    price_score * self.price_weight +
                    power_score * self.power_match_weight +
                    desc_score * self.description_weight
                )
                
                # Adicionar bônus para correspondência exata do tipo de telhado
                if roof_type and roof_type.lower() in (kit.get('title', '') + kit.get('description', '')).lower():
                    final_score *= 1.1
                    
                scored_kits.append({
                    **kit,
                    'score': final_score,
                    'price_score': price_score,
                    'power_score': power_score,
                    'description_score': desc_score
                })
                
            except Exception as e:
                print(f"Erro ao classificar kit: {e}")
                continue
                
        # Ordenar por pontuação
        scored_kits.sort(key=lambda x: x.get('score', 0), reverse=True)
        return scored_kits
