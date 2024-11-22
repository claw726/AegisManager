import requests
from typing import List, Dict

class Character:
  def __init__(self, first_name: str, last_name: str, email: str):
      self.first_name = first_name
      self.last_name = last_name
      self.email = email
      self.full_name = f"{first_name} {last_name}"

def get_house_md_characters() -> List[Character]:
  """Returns a list of main characters from House M.D."""
  return [
      Character("Gregory", "House", "house@ppth.edu"),
      Character("James", "Wilson", "wilson@ppth.edu"),
      Character("Lisa", "Cuddy", "cuddy@ppth.edu"),
      Character("Eric", "Foreman", "foreman@ppth.edu"),
      Character("Robert", "Chase", "chase@ppth.edu"),
      Character("Allison", "Cameron", "cameron@ppth.edu"),
      Character("Chris", "Taub", "taub@ppth.edu"),
      Character("Remy", "Hadley", "thirteen@ppth.edu"),
      Character("Lawrence", "Kutner", "kutner@ppth.edu")
  ]
def get_clear_skies_characters() -> List[Character]:
  """Returns a list of main characters from Clear Skies"""
  return [
      Character("John", "Rourke", "captain@clearskies.space"),
      Character("Solomon", "Burke", "burke@clearskies.space"),
      Character("Charlie", "Fodder", "fodder@clearskies.space"),
      Character("Tarquin", "Smith", "smith@clearskies.space"),
      Character("Sascha", "Culhane", "culhane@clearskies.space"),
      Character("Falcon", "Hausmann", "hausmann@clearskies.space"),
      Character("Guy", "Stone", "ghost@clearskies.space")
  ]


def get_base64_image(character_lastname: str) -> str:
  """Reads the base64 data URL from the generated text file"""
  base64_file = f"pictures_avif/{character_lastname.lower()}_base64.txt"
  try:
      with open(base64_file, 'r') as f:
          return f.read().strip()
  except FileNotFoundError:
      print(f"Warning: Base64 file not found for {character_lastname}")
      return ""

def create_user(character: Character, base_url: str) -> Dict:
  """Creates a user in the team management application."""
  
  # Construct the full URL
  url = f"{base_url}/api/auth/register"
  
  # Get the base64 image data
  profile_picture = get_base64_image(character.last_name)
  
  if not profile_picture:
      return {"success": False, "error": "Profile picture not found"}

  # Prepare the form data
  data = {
      "email": character.email,
      "name": character.full_name,
      "password": "password",
      "profilePicture": profile_picture
  }

  try:
      # Make the POST request
      response = requests.post(
          url,
          data=data,
          headers={
              "Content-Type": "application/x-www-form-urlencoded"
          },
          verify=False  # Since it's localhost with https
      )
      
      # Instead of checking status code, check if we got a response
      if response.text:  # or you could check for specific text in response.text
          print(f"Successfully created user: {character.full_name}")
          return {"success": True, "data": response.text}
      else:
          print(f"Failed to create user {character.full_name}. Response: {response.text}")
          return {"success": False, "error": response.text}

  except Exception as e:
      print(f"Error creating user {character.full_name}: {str(e)}")
      return {"success": False, "error": str(e)}

def main():
  base_url = "https://localhost:8444"
  
  # Get all characters
  characters = get_house_md_characters()
  characters += get_clear_skies_characters()
  
  # Create users for each character
  results = []
  for character in characters:
      result = create_user(character, base_url)
      results.append({
          "character": character.full_name,
          "result": result
      })
  
  # Print summary
  print("\nCreation Summary:")
  print("-" * 50)
  for result in results:
      status = "Success" if result["result"]["success"] else "Failed"
      print(f"{result['character']}: {status}")

if __name__ == "__main__":
  main()