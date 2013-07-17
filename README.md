projectGiw
==========

Sono presenti:
- due interfacce Java (InputRepository e OutputRepository) per scrivere e leggere dati da un repository di documenti social (contenenti dati su tweet e utenti).
- un insieme di tweet
- una classe astratta che si occupa di validare le implementazioni di InputRepository e OutputRepository
- una classe astratta che si occupa di generare diagrammi con i risultati di performance 

Dall'insieme di tweet, dovrete fissare delle relazioni arbitrarie di follower tra gli utenti che li hanno scritti (secondo le modalità descritte nella descrizione iniziale della tesina).
In questo modo otterrete il vostro insieme di dati di input.

Tenendo presente come si vuole interrogare il repository (guardando le API in OutputRepository), dovrete implementare InputRepository e OutputRepository usando ElasticSearch.
Una volta validate le implementazioni, potete far girare i performance test per avere i diagrammi.

Ogni diagramma descrive l'andamento con il crescere del repository di un metodo delle API di InputRepository e OutputRepository. Ad es., ci sarà un diagramma per il metodo getTweetById di OutputRepository. 
E ogni diagramma sarà comparativo, conterrà cioè tutti gli andamenti di tutte le implementazioni insieme. 
Potete quindi creare varie implementazioni, che metterete a confronto nei performance test, facendole gareggiare tra loro.
L'obiettivo finale del progetto è arrivare alla migliore implementazione considerando tutti i test di performance (o, ove non sia possibile, solo quelli di InputRepository o OutputRepository).
