# import modules; set up logging
import gensim, logging, os, sys, gzip, traceback
import random
logging.basicConfig(format='%(asctime)s : %(levelname)s : %(message)s',filename='word2vec.out', level=logging.INFO)


# A class to iterate through all walk files
class MySentences(object):
    def __init__(self, walks):
        self.walks = walks
    
    def __iter__(self):
        try:
            for walk in self.walks:
                    yield walk
        except Exception:
            traceback.print_exc()

#Construct walks
alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
walks = []
for i in range(1000):
    randomStart = random.randint(0, len(alphabet))
    depth = 10
    j = 0
    walk = []
    while j < depth:
        letter = alphabet[(randomStart + j) % len(alphabet)]
        walk.append(letter)
        j += 1
    walks.append(walk)

#print(walks)

# Construct sentences out of all the walk files
sentences = MySentences(walks)

# Build model and train
model = gensim.models.Word2Vec(size=100, workers=10, window=3, sg=1, negative=15, iter=5)
model.build_vocab(sentences)
model.train(sentences=sentences, total_examples=model.corpus_count, total_words=model.corpus_count, epochs=model.iter)

# Export Vectors
i = 1
my_vocab = list(model.wv.vocab)
logs = []
for word in my_vocab:
    with open('Vectors2/word_' + str(i) + '.vec', 'w') as file:
        logs.append("Exporting " + str(word))
        file.write(str(word) + "\n")
        file.write(str(model[word]))
    i += 1


# Export log file
with open("export.log", "w") as file:
    for log in logs:
        file.write(str(log) + "\n")


# Check for similarity
print(model.wv.most_similar(positive="A"))
