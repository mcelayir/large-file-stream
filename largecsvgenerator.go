package main

import (
	"bufio"
	"fmt"
	"math/rand"
	"os"
	"strconv"
	"time"
)

func main() {
	fileSize := int64(10e9) // 10GB
	f, err := os.Create("./largefile.csv")
	if err != nil {
		fmt.Println(err)
		return
	}
	w := bufio.NewWriter(f)

	countries := []string{"NL", "US", "DE"}
	rand.Seed(time.Now().UnixNano())
	size := int64(0)
	for size < fileSize {
		userID := int(rand.Int31n(100)) + 1
		country := countries[int(rand.Int31n(int32(len(countries))))]
		line := strconv.Itoa(userID) + "," + country + "\n"
		n, err := w.WriteString(line)
		if err != nil {
			fmt.Println(n, err)
			return
		}
		size += int64(len(line))
	}
	err = w.Flush()
	if err != nil {
		fmt.Println(err)
		return
	}
	err = f.Close()
	if err != nil {
		fmt.Println(err)
		return
	}
	fmt.Println("Size:", size)
}
