#!/bin/bash

# Create output directory if it doesn't exist
mkdir -p pictures_avif

# List of House M.D. characters
characters=(
	"house"
	"wilson"
	"cuddy"
	"foreman"
	"chase"
	"cameron"
	"taub"
	"hadley"
	"kutner"
)

for character in "${characters[@]}"; do
	input_file="pictures/${character}.jpg"
	output_file="pictures_avif/${character}.avif"

	if [[ ! -f ${input_file} ]]; then
		echo "Warning: ${input_file} not found, skipping..."
		continue
	fi

	echo "Converting ${character}..."

	# Convert to AVIF using FFmpeg
	# -c:v libaom-av1: Use AV1 codec
	# -still-picture 1: Optimize for still images
	# -crf 30: Control quality (lower number = higher quality, range 0-63)
	# -b:v 0: Use constant quality mode
	ffmpeg -i "${input_file}" \
		-vf "scale=256:256:force_original_aspect_ratio=increase,crop=256:256" \
		-c:v libaom-av1 \
		-still-picture 1 \
		-crf 30 \
		-b:v 0 \
		"${output_file}"

	# Convert to base64
	base64_data=$(base64 -i "${output_file}")
	echo "data:image/avif;base64,${base64_data}" >"pictures_avif/${character}_base64.txt"

	# Get file size
	size=$(stat -f%z "${output_file}")
	echo "Completed ${character}:"
	echo "  - Size: $((size / 1024))KB"
	echo
done

echo "All images processed!"
echo "Created files:"
ls -lh pictures_avif/*.avif
