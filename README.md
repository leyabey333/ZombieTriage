# ZombieTriage
This is a java application that can be used in the scenario in which you need to solve a triage problem by utilizing utilitarianism.

## Scenario 
Patients are prioritized using a PriorityQueue so that the least severe patients are handled first. If two patients have the same severity, 
the one who arrived earlier is treated first. The hospital also includes a helicopter evacuation system that arrives every 12 hours and can transport up to 5 patients at a time. 

## Structure Reasoning
The application uses a PriorityQueue to efficiently manage patients so that those with the lower
priority ranks are treated first. Patients are compared based on priority rank, with 
ties broken by earliest arrival time to ensure fairness. The system is structured around seperate
classes to handle patient data, hospital operations, and time-based helicopter 
pickups, making it organized and scalable. 

## Triage Rules
Patients are assigned a priority tier based on the seriousness of their condition. Lower ranks are seen sooner. If two patients
have the same priority rank, the patient who was admitted earlier is treated first. 
- Rank 1: Recently infected/very low severity
- Rank 2: Mildly wounded
- Rank 3: Minor illness or cuts
- Rank 4: Moderate injury
- Rank 5: Stable but needs treatment
- Rank 6: Serious injury
- Rank 7: Severe infection or trauma
- Rank 8: Critical condition
- Rank 9: Extremely critical condition 
- Rank 10: Most severe condition in the queue


## Requirements
Java 17 or recent

## Build Instructions 
```
To build: javac -d out src/*.java
To run: java -cp out JavaTriageApp
```
## Using the App
On launch, the application will print a menu of options to console. Input the number associated with the desired
option to proceed. 
Follow the prompts for each selection. You can add patients, view or treat patients based on priority, check 
hospital status, advance time, and trigger helicopter pickups when available. 
The menu will continue to display after each action until you choose to exit the application. 


