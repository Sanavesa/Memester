# import modules; set up logging
import gensim, logging, os, sys, gzip, traceback
import json
logging.basicConfig(format='%(asctime)s : %(levelname)s : %(message)s',filename='word2vec.out', level=logging.INFO)

# load config file for the settings of the training
config_json = ""
try:
    config_file = open('config.txt', mode='r')
    config_json = config_file.read()
    config_file.close()
    config_json = json.loads(config_json) # convert json string to python dictionary
except FileNotFoundError:
    config_file = open('config.txt', mode='w')
    config = {
        "size": 500,
        "workers": 5,
        "window": 10,
        "sg": 1,
        "negative": 15,
        "iterations": 5,
    }
    config = json.dumps(config, indent=4, sort_keys=True)
    config_file.write(config)
    config_file.close()
    config_json = json.loads(config) # convert json string to python dictionary

# Get a list of all walk files (only with extension .walk)
files = []
for r, d, f in os.walk("../Walks/"):
    for file in f:
        if ".walk" in file:
            files.append(os.path.join(r, file))

# A class to iterate through all walk files
class MySentences(object):
    def __init__(self, filenames):
        self.filenames = filenames
    
    def __iter__(self):
        try:
            for file in self.filenames:
                with open(file) as fp:
                    for line in fp:
                        line = line.rstrip('\n')
                        words = line.split("->")
                        yield words
        except Exception:
            traceback.print_exc()

# Construct sentences out of all the walk files
sentences = MySentences(files)

# Build model and train
model = gensim.models.Word2Vec(size=config_json["size"], workers=config_json["workers"], window=config_json["window"], sg=config_json["sg"], negative=config_json["negative"], iter=config_json["iterations"])
model.build_vocab(sentences)
model.train(sentences=sentences, total_examples=model.corpus_count, total_words=model.corpus_count, epochs=model.iter)

# Export Vectors
i = 1
my_vocab = list(model.wv.vocab)
logs = []
for word in my_vocab:
    if "http://erau.edu/ontology/meme.owl#" not in word or "Meme" not in word or "http://erau.edu/ontology/meme.owl#Meme" in word:
        logs.append("Skipping " + str(word))
        continue

    with open('../Vectors/word_' + str(i) + '.vec', 'w') as file:
        logs.append("Exporting " + str(word))
        file.write(str(word) + "\n")
        file.write(str(model[word]))
    i += 1


# Export log file
with open("export.log", "w") as file:
    for log in logs:
        file.write(str(log) + "\n")
