# some_file.py
import sys
# insert at 1, 0 is the script path (or '' in REPL)
sys.path.insert(1, '/home/nikit/Workplace/Thesis/ernie/codebase/pretrain_data')
import tokenization
import os
from multiprocessing import Pool
from nltk.tokenize import sent_tokenize
import math
import json
import logging

logger = logging.Logger('catch_all')
# TODO: update the vocab
vocab_file = "ernie_base/vocab.txt"
out_folder = "pretrain_data/dbpabs_raw/"
input_folder = "/home/nikit/Workplace/Thesis/dbpabs_ernie_raw/json/"

if not os.path.exists(out_folder):
    os.makedirs(out_folder)

do_lower_case = True
tokenizer = tokenization.FullTokenizer(
      vocab_file=vocab_file, do_lower_case=do_lower_case)

n = int(sys.argv[1])

file_list = []
for path, _, filenames in os.walk(input_folder):
    for filename in filenames:
        file_list.append(os.path.join(path, filename))

part = int(math.ceil(len(file_list) / n))
file_list = [file_list[i:i+part] for i in range(0, len(file_list), part)]

def absFileHandler(idx, file_list):
    etc = None
    try:
        print("Process started: "+str(idx)+"\tNumber of files to process: "+str(len(file_list)))
        target = "{}/{}".format(out_folder, idx)

        for input_name in file_list:
            print("Process-"+str(idx)+" processing file: "+input_name)
            data = None
            abstract = {}

            fout_text = open(target + "_token", "a")
            fout_ent = open(target + "_entity", "a")

            with open(input_name) as json_file:
                data = json.load(json_file)

            for abstract in data:
                sentences = sent_tokenize(abstract['content'])
                # For each mention create an array of tokens and entityUrl
                mentions = []
                # skip if no annotations are present
                if abstract['mentions'] == None:
                    continue
                mentions.extend(abstract['mentions'])
                # sorting based on start position of mention (occurence)
                mentions = sorted(mentions, key=lambda k: k['startPos'])
                mentionTokensArr = []
                mentionDetArr = []
                for i in range(len(mentions)):
                    mention = mentions[i]
                    mentionTokens = tokenizer.tokenize(mention['surfaceForm'])
                    # only if tokenizer is able to tokenize the surface form
                    if len(mentionTokens) > 0 :
                        mentionTokensArr.append(mentionTokens)
                        mentionDetArr.append(mention)

                text_out = [len(sentences)]
                ent_out = [len(sentences)]

                new_text_out = []
                new_ent_out = []

                for sent in sentences:
                    # print(len(mentionTokensArr))
                    tokens = tokenizer.tokenize(sent)
                    new_text_out = []
                    new_ent_out = []
                    i = 0
                    totEntFound = 0
                    while i < len(tokens):
                        tok = tokens[i]

                        # fetch the tokens for first entity in queue
                        entityToCheck = None
                        if len(mentionTokensArr) > 0:
                            entityToCheck = mentionTokensArr[0]
                        # check if first entity token matches the current sentence token
                        etc = mentionTokensArr
                        if (entityToCheck != None) and (tok == entityToCheck[0]):
                            totEntFound = totEntFound + 1
                            tokArrLen = len(entityToCheck)
                            curMention = mentionDetArr[0]
                            entityUri = curMention['entityUri']
                            # print("entity found: "+entityUri+" at index: "+str(i))
                            # ( Append new_ent_out with entity uri )x size of entity tokens
                            j = 0
                            while j < tokArrLen:
                                new_ent_out.append(entityUri)
                                j = j + 1
                            # ( Append new_text_out with token ids of next n (size of entity tokens) tokens
                            new_text_out.extend(tokenizer.convert_tokens_to_ids(entityToCheck))
                            # increment i by n
                            nextMention = None
                            if len(mentionDetArr) > 1:
                                nextMention = mentionDetArr[1]
                            if (nextMention == None) or (nextMention['startPos'] != curMention['startPos']):
                                i = i + tokArrLen
                                # remove first item from both queues
                            del mentionTokensArr[0]
                            del mentionDetArr[0]
                            # if(nextMention != None) :
                            #    print("next in line: "+nextMention['entityUri'])
                            # print("jumping to index: "+str(i))
                        else:
                            # Append new_ent_out with unk
                            new_ent_out.append("#UNK#")
                            # append new text out with token id of current token
                            new_text_out.append(tokenizer.convert_tokens_to_ids([tok])[0])
                            # increment i
                            i = i + 1

                    if totEntFound > 2 and len(new_ent_out) != 0:
                        ent_out.append(len(new_ent_out))
                        ent_out.extend(new_ent_out)
                        text_out.append(len(new_text_out))
                        text_out.extend(new_text_out)
                    else:
                        # decrement the number sentences mentioned before
                        text_out[0] -= 1
                        ent_out[0] -= 1

                fout_ent.write("\t".join([str(x) for x in ent_out]) + "\n")
                fout_text.write("\t".join([str(x) for x in text_out]) + "\n")
            fout_ent.close()
            fout_text.close()
    except Exception as e:  # work on python 3.x
        print('Process failed with error: ' + str(e))
        print(etc)
        logger.error(e, exc_info=True)

p = Pool(n)
for i in range(n):
    p.apply_async(absFileHandler, args=(i, file_list[i]))
p.close()
p.join()