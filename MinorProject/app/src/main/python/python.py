import face_recognition


def main(image_path_1, image_path_2):

    try:
        # Load the images and convert them to face encodings
        image_1 = face_recognition.load_image_file(image_path_1)
        face_encoding_1 = face_recognition.face_encodings(image_1)[0]

        image_2 = face_recognition.load_image_file(image_path_2)
        face_encoding_2 = face_recognition.face_encodings(image_2)[0]

        # Compare the face encodings and return True if they match, False otherwise
        results = face_recognition.compare_faces([face_encoding_1], face_encoding_2)
    except:
        results=[False]

    return results[0]