'''
Created on Feb 16, 2016

@author: petar
@modified by Mohammad

@documentation is at https://www.pydoc.io/pypi/gensim-3.2.0/autoapi/models/word2vec/index.html
'''
# import modules; set up logging
import gensim, logging, os, sys, gzip, traceback
logging.basicConfig(format='%(asctime)s : %(levelname)s : %(message)s',filename='word2vec.out', level=logging.INFO)

class MySentences(object):

    def __init__(self, fname):
        self.fname = fname
    
    def __iter__(self):
        print("Reading file: " + self.fname)
        try:
            with open(self.fname) as fp:
                for line in fp:
                    line = line.rstrip('\n')
                    words = line.split("->")
                    #print(words)
                    yield words
        except Exception:
            print("Failed reading file: " + self.fname)
            traceback.print_exc()
                
sentences = MySentences('walks.txt') # a memory-friendly iterator
#sg 500

# Mohammad Comments:
'''
    -size
        is the dimensionality of the feature vectors. (size of the output vector)

    -workers
        is the number of threads used

    -window
        is the maximum distance between current and predicted word within a sentence
        think of it as field of view in the sentence

    -sg
        defines the training algorithm. sg=0 is CBOW, whereas sg=1 is skip-gram

    -negative
        uses negative sampling (google it)

    -iter
        number of iterations (epochs) over the corpus
'''
model = gensim.models.Word2Vec(size=500, workers=5, window=10, sg=1, negative=15, iter=5)
model.build_vocab(sentences)
model.train(sentences=sentences, total_examples=model.corpus_count, total_words=model.corpus_count, epochs=model.iter)

#sg/cbow features iterations window negative hops random walks
model.save('model.bin')

print("-------------------------------")

my_vocab = list(model.wv.vocab)

#print("Vocab is: ")
#print(my_vocab)

# Save Vectors
i = 1
for word in my_vocab:
    with open('vectors/word_' + str(i) + '.vec', 'w') as file:
        file.write(str(word) + "\n")
        file.write(str(model[word]))
    i += 1

#new_model = gensim.models.Word2Vec.load('model.bin')
#print(new_model)


'''
#sg 200
model1 = gensim.models.Word2Vec(size=200, workers=5, window=5, sg=1, negative=15, iter=5)
model1.reset_from(model)


#cbow 500
model2 = gensim.models.Word2Vec(size=500, workers=5, window=5, sg=0, iter=5,cbow_mean=1, alpha = 0.05)
model2.reset_from(model)


#cbow 200
model3 = gensim.models.Word2Vec(size=200, workers=5, window=5, sg=0, iter=5, cbow_mean=1, alpha = 0.05)
model3.reset_from(model)

del model

model1.train(sentences)
model1.save('DB2Vec_sg_200_5_5_15_2_500')

del model1

model2.train(sentences)
model2.save('DB2Vec_cbow_500_5_5_2_500')

del model2

model3.train(sentences)
model3.save('DB2Vec_cbow_200_5_5_2_500')
'''
