import { imageCompression } from 'browser-image-compression';

async function handleFileUpload(event) {
    const file = event.target.files[0];
    if (file) {
      try {
        console.log('File selected:', file);
        const options = {
          maxSizeMB: 0.064,
          maxWidthOrHeight: 512,
          useWebWorker: true,
        };
        console.log('Compressing file...');
        const compressedFile = await imageCompression(file, options);
        console.log('File compressed successfully');
        const reader = new FileReader();
        reader.onload = async (e) => {
          const imageDataUrl = e.target.result;
          console.log('Reading compressed file as data URL...');
          const image = new Image();
          image.onload = async () => {
            console.log('Image loaded, cropping to 1:1 aspect ratio...');
            const canvas = document.createElement('canvas');
            const ctx = canvas.getContext('2d');
            const { width: imageWidth, height: imageHeight } = image;
            const aspectRatio = imageWidth / imageHeight;
            let newWidth, newHeight;
            if (aspectRatio < 1) {
              newWidth = imageWidth;
              newHeight = newWidth;
            } else {
              newWidth = imageHeight;
              newHeight = newWidth;
            }
            const x = (imageWidth - newWidth) / 2;
            const y = (imageHeight - newHeight) / 2;
            canvas.width = newWidth;
            canvas.height = newHeight;
            ctx.drawImage(image, x, y, newWidth, newHeight, 0, 0, newWidth, newHeight);
            const croppedDataURL = canvas.toDataURL('image/jpeg', 0.92);
            console.log('Image cropped successfully');
            const blob = await fetch(croppedDataURL).then(res => res.blob());
            const newFile = new File([blob], file.name, { type: 'image/jpeg' });
            console.log('Creating new file from blob...');
            try {
              const compressedCroppedFile = await imageCompression(newFile, options);
              console.log('Compressing cropped file...');
              const reader = new FileReader();
              reader.onload = (e) => {
                console.log('Reading compressed cropped file as data URL...');
                return e.target.result;
              };
              reader.readAsDataURL(compressedCroppedFile);
            } catch (error) {
              console.error('Error compressing cropped file:', error);
              return null;
            }
          };
          image.src = imageDataUrl;
        };
        reader.readAsDataURL(compressedFile);
      } catch (error) {
        console.error('Error compressing image:', error);
        return null;
      }
    } else {
      console.log('No file selected');
      return null;
    }
  }
  
  export default handleFileUpload;