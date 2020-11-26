import indexed_dataset
import os

builder = indexed_dataset.IndexedDatasetBuilder('pretrain_data/merge_dbp.bin')
for filename in os.listdir("pretrain_data/dbpabs_data"):
    if filename[-4:] == '.bin':
        builder.merge_file_("pretrain_data/dbpabs_data/"+filename[:-4])
builder.finalize("pretrain_data/merge_dbp.idx")