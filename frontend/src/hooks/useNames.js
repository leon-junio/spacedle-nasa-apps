import useSWR from "swr";
const fetcher = (...args) => fetch(...args).then((res) => res.json());

const useImage = (type) => {
  const { data, error } = useSWR(
    `https://spacedle.herokuapp.com/api/names/${type}`,
    fetcher,
    {
      revalidateOnFocus: false,
      revalidateOnReconnect: false,
      revalidateIfStale: false,
    }
  );

  return {
    data: data,
    loading: !error && !data,
    error: error,
  };
};

export default useImage;
