def call() {
  final pythonContent = libraryResource('pythonScript.py')
  writeFile(file: 'pythonScript.py', text: pythonContent)
  sh('chmod +x pythonScript.py && ./pythonScript.py')
}