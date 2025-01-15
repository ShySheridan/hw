import os
import json
import yaml 

def json_to_yaml(json_input, yaml_output):
    
    with open(json_input, 'r', encoding='utf-8') as json_file:
        data = json.load(json_file)
    
    yaml_data = yaml.dump(data, allow_unicode=True, default_flow_style=False, sort_keys=False)
    
    with open(yaml_output, 'w', encoding='utf-8') as yaml_file:
        yaml_file.write('---\n')  
        yaml_file.write(yaml_data)
    
    print(f"YAML сохранен в '{yaml_output}'.")

def main():
    script_dir = os.path.dirname(os.path.abspath(__file__))
    json_file = os.path.join(script_dir, 'schedule.json')
    yaml_file = os.path.join(script_dir, 'schedule.yaml')  
    
    json_to_yaml(json_file, yaml_file)


main()
